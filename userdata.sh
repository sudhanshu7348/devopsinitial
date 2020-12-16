#! /bin/bash -xe
#######################################
# Provisioning steps:
# ----------------------
# 1. Configure weekly cron jobs for security updates
# 2. Mount EFS
#  2.1 Initialize service directories
# 3. Install Docker
#  3.1 Set Docker lib dir at /var/lib/docker
#  3.2 Start Docker with auto start on boot
#  3.3 Create soft link to EFS directory for docker
#  3.4 Give user Ubuntu permissions to run docker without sudo 
# 4. Run swarm autodiscovery helper (image located in ECR. Source code found in /aws-swarm-init/ of this repo)
#  4.1 Install AWS CLI (pip3 required)
#  4.2 Log into ECR
#  4.3 Pull image(s)
#  4.4 Run container
# 5. Install New Relic
#  5.1 Install GO
#######################################

SWARM_ROLE=${ROLE}
EFS_ID=${EFS_ID}
SWARM_DISCOVERY_BUCKET=${SWARM_DISCOVERY_BUCKET}
REGION=${REGION}

PRIVATE_IP=$(curl -fsS http://instance-data/latest/meta-data/local-ipv4)
EFS_DIR=/mnt/efs
ARTIFACTORY_REPO="hc-us-east-aws-artifactory.cloud.health.ge.com"
SWARM_INIT_IMAGE="docker-dtt-preprod/aws-swarm-init:latest"
SSM_ARTIFACTORY_PATH="/provisioner/cloud-ops/swarm/preprod/artifactory"

# Create a weekly cron job to give us the latest security updates
echo "/usr/bin/apt-get update --security -y" > /etc/cron.weekly/apt-getsecurity.cron
set -e
dpkg --configure -a

# Put in a timer to wait for all services to be properly provisioned in AWS to use

# Install docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
apt-key fingerprint 0EBFCD88
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt-get update -y
while ! apt-get install docker-ce docker-ce-cli containerd.io python3 python-botocore python-pip -y ; do sleep 1; done
yes | pip install awscli --upgrade --ignore-installed six

# We want experimental support to allow us to use "docker service logs <name>"
echo '{"experimental": true, "log-driver": "json-file", "log-opts": {"max-size": "10m", "max-file": "3" }}' > /etc/docker/daemon.json
sudo service docker start && update-rc.d docker start 20
usermod -a -G docker ubuntu
chown -R 1000:1000 /var/run/docker.sock
# Run this every hour to clean them up (cleans everything not actively being used)
echo -e '#!'"/bin/bash\ndocker system prune -f -a --volumes > /dev/null" > /etc/cron.hourly/docker-cleanup
chmod -R 755 /etc/cron.hourly/

# Run autodiscovery swarm code located in Artifactory
USERNAME="$(aws ssm get-parameter --name $SSM_ARTIFACTORY_PATH/username --region $REGION --output text --query Parameter.Value)"
PASSWORD="$(aws ssm get-parameter --name $SSM_ARTIFACTORY_PATH/password --region $REGION --output text --query Parameter.Value --with-decryption)"

apt remove docker-compose -y && apt autoremove -y
while ! docker login $ARTIFACTORY_REPO -u $USERNAME -p $PASSWORD ; do sleep 1; done
while ! docker pull $ARTIFACTORY_REPO/$SWARM_INIT_IMAGE ; do sleep 1; done
chown -R 1000:1000 /usr/local/bin/aws
# Get local ip from the instance metadata, we need to specify this when creating/joining the swarm
# One shot a custom container with a small script to automatically init swarm, using S3 for discovery
docker run -d --restart on-failure:15 \
    -e SWARM_DISCOVERY_BUCKET=$SWARM_DISCOVERY_BUCKET \
    -e ROLE=$SWARM_ROLE \
    -e NODE_IP=$PRIVATE_IP \
    -v /var/run/docker.sock:/var/run/docker.sock \
    $ARTIFACTORY_REPO/$SWARM_INIT_IMAGE

# Mount EFS on all nodes 
while ! apt-get install nfs-common -y ; do sleep 1; done
mkdir -p $EFS_DIR
while ! mount -t nfs -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2,noresvport $EFS_ID.efs.$REGION.amazonaws.com:/ $EFS_DIR ; do sleep 1; done

echo "$EFS_ID:/ $EFS_DIR" >> /etc/fstab
# Initial EFS directories setup for core services
mkdir -p $EFS_DIR/elasticstack/{elasticsearch,logstash,kibana,beats/{filebeat,metricbeat}}
chown -R root $EFS_DIR/elasticstack/beats/


mkdir -p $EFS_DIR/portainer 
if [[ $ENV -ne "prod" ]]; then
    mkdir -p $EFS_DIR/jenkins_home 
    mkdir -p $EFS_DIR/sonarqube/data 
fi

# Set port permissions for directories to be consumed by stack
# Create soft link to Docker dir
mkdir -p /etc/systemd/system/docker.service.d/
touch /etc/systemd/system/docker.service.d/docker.conf
echo $'[SERVICE]\nExecStart=\nExecStart=/usr/bin/dockerd -H unix:///var/run/docker.sock -H tcp://0.0.0.0:2375' >> /etc/systemd/system/docker.service.d/docker.conf

# New Relic
echo "license_key: 4d9c94ef42367f | sudo tee -a /etc/newrelic-infra.yml
curl https://download.newrelic.com/infrastructure_agent/gpg/newrelic-infra.gpg | sudo apt-key add -
printf "deb [arch=amd64] https://download.newrelic.com/infrastructure_agent/linux/apt bionic main" | sudo tee -a /etc/apt/sources.list.d/newrelic-infra.list
apt-get update
apt-get install newrelic-infra -y
apt-get install golang-go -y
export GOROOT=/usr/local/go
export GOPATH=$HOME/Projects/Proj1
export PATH=$GOPATH/bin:$GOROOT/bin:$PATH
