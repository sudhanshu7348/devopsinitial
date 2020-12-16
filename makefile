MASG := `cat .terraform-output-cache | jq -r '."manager-autoscaling-group-name".value'`
ASG := `cat .terraform-output-cache | jq -r '."autoscaling-group-names".value[]' | xargs printf "%s,"`

KEY_PATH := `cat .terraform-output-cache | jq -r '."key-path".value'`
S3BUCKET := `cat .terraform-output-cache | jq -r '."discovery-bucket-name".value'`

INSTANCE_QUERY := '[PrivateIpAddress,InstanceId,Tags[?Key==`Name`].Value | [0],Tags[?Key==`SwarmRole`].Value | [0],State.Name]'
IP_ONLY_INSTANCE_QUERY := '[PrivateIpAddress]'
SWARM_MANAGER := `make swarm-manager`

STACK_NAME=app

swarm-deploy:
	scp docker/docker-compose.yml ${SWARM_MANAGER}:docker-compose.yml
	ssh ${SWARM_MANAGER} docker stack deploy --compose-file docker-compose.yml ${STACK_NAME}

swarm-stop:
	ssh ${SWARM_MANAGER} docker stack rm ${STACK_NAME}

swarm-status:
	ssh ${SWARM_MANAGER} docker node ls

swarm-instances:
	@scripts/sandbox/aws-list-asg-instances ${ASG} ${INSTANCE_QUERY} | sed 's/^/ec2-user@/'

swarm-managers:
	@scripts/sandbox/aws-list-asg-instances ${MASG} ${INSTANCE_QUERY} | sed 's/^/ec2-user@/'

swarm-managers-provision:
	@scripts/sandbox/aws-provision-nodes ${MASG} ${IP_ONLY_INSTANCE_QUERY} ${KEY_PATH} ${S3BUCKET} "manager" 

swarm-manager:
	@scripts/sandbox/aws-list-asg-instances ${MASG} '[PublicIpAddress]' | sed 's/^/ec2-user@/' | head -n1

swarm-ssh:
	ssh ${SWARM_MANAGER}

swarm-remove-instance:
	@scripts/drain-node ${ID}
	aws autoscaling set-instance-health --instance-id ${ID} --health-status Unhealthy --no-should-respect-grace-period

swarm-tidy:
	@scripts/swarm-remove-down-nodes

.PHONY: swarm-deploy swarm-stop swarm-status swarm-instances swarm-managers swarm-manager swarm-ssh swarm-remove-instance swarm-clean
