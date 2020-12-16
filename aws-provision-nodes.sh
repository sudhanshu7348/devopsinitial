#! /bin/bash

GROUP_QUERY="AutoScalingGroups[*].Instances[*].InstanceId"
INSTANCE_QUERY="Reservations[*].Instances[*]"
EFS_ID=`cat .terraform-output-cache | jq -r '."efs-id".value'`
EFS_DIR="/mnt/efs"
USER="ubuntu"

KEY_PATH=$3
SWARM_DISCOVERY_BUCKET=$4
ROLE=$5

if [[ "$2" != "" ]]; then
  INSTANCE_QUERY="$INSTANCE_QUERY.$2"
fi

INSTANCES=$(aws autoscaling describe-auto-scaling-groups --auto-scaling-group-names "$1" --output text --query "$GROUP_QUERY")

INSTANCE_IPS=$(aws ec2 describe-instances \
  --filters Name=instance-state-name,Values=running \
  --instance-ids $INSTANCES \
  --output text \
  --query "$INSTANCE_QUERY")

echo $(pwd)
for ip in $INSTANCE_IPS
do 
  echo $"\n\n${KEY_PATH} | ${USER} | ${ip} | ${SWARM_DISCOVERY_BUCKET} | ${ROLE}\n\n"
    ssh -i ${KEY_PATH} $USER@$ip 'bash -s' < ./scripts/provision/auto-join.sh $SWARM_DISCOVERY_BUCKET $ROLE
    # ssh -i ${KEY_PATH} $USER@$ip 'bash -s' < ./scripts/provision/efs-mount.sh $EFS_DIR $EFS_ID
done
