#!groovy
def call(Map pipelineParams) {
    """
    export PROJECT_VERSION=${pipelineParams.projectVersion}
    export ARTIFACT_NAME=${pipelineParams.artifactName}
    export DOCKER_REGISTRY=${env.dockerRegistry}
    export BF_PREPROD_DOCKER_REPO=${pipelineParams.dockerRepo}
    export PREPROD_DOCKER_REPO=${pipelineParams.dockerRepo}
    export DOCKER_REPO=${pipelineParams.dockerRepo}
    export APP_BRANCH=${pipelineParams.appBranch}
    export PORT=${pipelineParams.port}
    export SERVICE_NAME=${pipelineParams.serviceName}
    export HOST=${pipelineParams.host}
    export ENV_FILE=${pipelineParams.envFile}
    """
}
