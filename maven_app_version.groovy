#!groovy
def call(Map pipelineParams) {
    script {
        pipelineParams.projectVersion = readMavenPom().getVersion() 
        pipelineParams.artifactName = readMavenPom().getArtifactId()
    }
}
