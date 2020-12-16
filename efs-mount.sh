# MOUNTING OF THE EFS MUST OCCUR AT THE USER-DATA STAGE

# # #!/bin/bash
# EFS_DIR=$1
# EFS_ID=$2
# REGION="us-east-1"
# PRIVATE_IP=$(curl -fsS http://instance-data/latest/meta-data/local-ipv4)

# apt-get install nfs-common -y
# mkdir -p $EFS_DIR
# for iteration in 5; do
#     mount -t nfs -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2,noresvport ${EFS_ID}.efs.${REGION}.amazonaws.com:/ $EFS_DIR && break || sleep 15
# done
# chown -R 1000:1000 $EFS_DIR

# # Initial EFS directories setup for core services
# # TODO: check for environment, as we will not need jenkins OR portainer on PROD
# mkdir -p $EFS_DIR/jenkins_home 
# mkdir -p $EFS_DIR/portainer 

# # Set port permissions for directories to be consumed by stack
# # Create soft link to Docker dir
# mkdir -p $EFS_DIR/$PRIVATE_IP/var/lib/docker
# ln -s /var/lib/docker $EFS_DIR/$PRIVATE_IP/var/lib/docker
# mkdir -p /etc/systemd/system/docker.service.d/
# touch /etc/systemd/system/docker.service.d/docker.conf
# echo $'[SERVICE]\nExecStart=\nExecStart=/usr/bin/dockerd\nExecStart=/usr/bin/dockerd -H unix:// -H tcp://0.0.0.0:2375' >> /etc/systemd/system/docker.service.d/docker.conf
