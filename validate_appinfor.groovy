#!groovy
def call(Map pipelineParams) {
    script {
        pipelineParams.projectVersion = pipelineParams.projectVersion.toLowerCase()
        pipelineParams.artifactName = pipelineParams.artifactName.toLowerCase()
    }
}
