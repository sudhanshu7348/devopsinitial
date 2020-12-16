#! /bin/bash

## Prereqs: awscli, Python3 (needed for awscli), docker
PRIVATE_IP=$(curl -fsS http://instance-data/latest/meta-data/local-ipv4)
ECR_LOGIN=$(aws ecr get-login --region us-east-1 --no-include-email)
SWARM_DISCOVERY_BUCKET=$1
ROLE=$2

for iteration in 5; do
    ${ECR_LOGIN} && break || sleep 15
done

for iteration in 5; do
    docker pull 597695158961.dkr.ecr.us-east-1.amazonaws.com/stratos-docker-images/aws-swarm-init:latest  && break || sleep 15
done

docker run -d --restart on-failure:15 \
    -e SWARM_DISCOVERY_BUCKET=${SWARM_DISCOVERY_BUCKET} \
    -e ROLE=${ROLE} \
    -e NODE_IP=${PRIVATE_IP} \
    -v /var/run/docker.sock:/var/run/docker.sock \
    597695158961.dkr.ecr.us-east-1.amazonaws.com/stratos-docker-images/aws-swarm-init
