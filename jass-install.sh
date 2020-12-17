#! /bin/bash

# mkdir -p /var/jenkins_home

# docker service create \
#     --name jenkins \
#     --hostname jenkins \
#     --network gehc-bf \
#     --mode replicated \
#     --replicas 1 \
#     --constraint "node.hostname == ip-10-242-69-47" \
#     --limit-memory 4g \
#     --reserve-memory 300m \
#     --update-parallelism 1  \
#     --update-delay 10s \
#     --restart-condition any \
#     --endpoint-mode vip  \
#     --with-registry-auth \
#     --label traefik.port=8095 \
#     --label traefik.frontend.passHostHeader=true \
#     --label traefik.docker.network=gehc-bf \
#     -e JENKINS_OPTS="--httpPort=8095 --prefix=/jenkins-efs" \
    # --mount "type=bind,source=/var/jenkins_home,target=/var/jenkins_home" \
    # --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
    # --label "traefik.frontend.rule=Host:nprd.bf.cloud.health.ge.com"  hc-us-east-aws-artifactory.cloud.health.ge.com/docker-all/jaas/jenkins-master:1.0.201908

# TODO: make EFS mnt dir dynamic
mkdir -p ${efs_root_dir}/jenkins_home
chown -R 1000:1000 ${efs_root_dir}/jenkins_home
docker service create \
    --name jenkins-efs \
    --hostname jenkins-efs  \
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
    -e JENKINS_OPTS="--httpPort=8085 --prefix=/jenkins-efs" \
    --mount "type=bind,source=${efs_root_dir}/jenkins_home,target=/var/jenkins_home" \
    --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
    --label "traefik.frontend.rule=Host:nprd.bf.cloud.health.ge.com"  hc-us-east-aws-artifactory.cloud.health.ge.com/docker-all/jaas/jenkins-master:1.0.201908
