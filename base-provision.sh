#!/bin/bash

service_name="bf_uaa"
network_name="gehc-bf"

$(aws ecr get-login --no-include-email --region us-east-1)  
docker pull 597695158961.dkr.ecr.us-east-1.amazonaws.com/bf/uaa/preprod:latest
mkdir -p /var/jenkins_home
# UAA
docker service create \
    --name bf-uaa \
    --detach=false \
    --hostname bf-uaa \
    --replicas 1 \
    --constraint "node.hostname == ip-10-242-69-47" \
    --network "gehc-bf" \
    --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
    --label traefik.port=8080 \
    --label traefik.frontend.passHostHeader=true \
    --label "traefik.frontend.rule=Host:nprd.bf.cloud.health.ge.com;PathPrefixStrip:/uaa/" \
    --label traefik.docker.network=gehc-bf \
    --with-registry-auth 597695158961.dkr.ecr.us-east-1.amazonaws.com/bf/uaa/preprod:latest

# PORTAINER
docker service create \
    --name portainer \
    --detach=false \
    --hostname portainer \
    --replicas 1 \
    --constraint "node.hostname == ip-10-242-69-47" \
    --network "gehc-bf" \
    --mount type=bind,source=/srv/data1/dockervols/portainer,target=/data \
    --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
    --label traefik.port=9000 \
    --label traefik.frontend.passHostHeader=true \
    --label "traefik.frontend.rule=Host:nprd.bf.cloud.health.ge.com;PathPrefixStrip:/portainer/" \
    --label traefik.docker.network=gehc-bf \
    --with-registry-auth nexus.gam.gehealthcare.com/portainer/portainer:1.19.2

openssl rand -base64 32

# # PORTAINER (DYNAMIC)
# docker service create \
#     --name portainer \
#     --detach=false \
#     --hostname portainer \
#     --replicas 1 \
    
#     --constraint "node.hostname == <MANAGER IP>" \
#     --network "<NETWORK NAME>" \
    
#     --mount type=bind,source=/srv/data1/dockervols/portainer,target=/data \
#     --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
#     --label traefik.port=9000 \
#     --label traefik.frontend.passHostHeader=true \
    
#     --label "traefik.frontend.rule=Host:<ALB DNS>;PathPrefixStrip:/portainer/" \
    
#     --label traefik.docker.network=<NETWORK NAME> \
    
#     --with-registry-auth nexus.gam.gehealthcare.com/portainer/portainer:1.19.2


# Jenkins (JaaS)
docker service create \
    --name jenkins \
    --hostname jenkins  \
    --network gehc-bf \
    --mode replicated \
    --replicas 1 \
    --constraint "node.hostname == ip-10-242-69-47" \
    --limit-memory 4g \
    --reserve-memory 300m \
    --update-parallelism 1  \
    --update-delay 10s \
    --restart-condition any \
    --endpoint-mode vip  \
    --with-registry-auth \
    --label traefik.port=8085 \
    --label traefik.frontend.passHostHeader=true \
    --label traefik.docker.network=gehc-bf \
    --label "traefik.frontend.rule=Host:nprd.bf.cloud.health.ge.com"  \
    --mount "type=bind,source=/var/jenkins_home,target=/var/jenkins_home" \
    --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
    -e JENKINS_OPTS="--httpPort=8085 --prefix=/jenkins" \
    hc-us-east-aws-artifactory.cloud.health.ge.com/docker-all/jaas/jenkins-master:1.0.201908
