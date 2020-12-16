!/bin/bash
export TERM=xterm
docker_server=nexus.gam.gehealthcare.com
docker_usr=docker-build
docker_pwd=$1

###########################
# Docker Login
###########################
docker login -u $docker_usr -p $docker_pwd $docker_server

networks=""
for net in $(docker network ls --filter "driver=overlay" --filter "name=$env_type-" --format {{.Name}})
do
networks+=" --network $net "
done

if [[ `docker service ls | grep docker-gateway | wc -l` -gt 0 ]]
then
docker service rm docker-gateway
fi

eval docker service create \
--hostname "{{.Service.Name}}-{{.Task.Slot}}" \
$networks \
--constraint "node.role==manager" \
--replicas 1 \
--restart-max-attempts 5 \
--publish 80:80 \
--publish 8080:8080 \
--mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
--name docker-gateway \
--with-registry-auth \
$docker_server/traefik:v1.1.2 \
--web --docker.swarmmode --docker.domain=docker-gateway --debug
