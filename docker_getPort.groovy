#!groovy
def call(Map pipelineParams) {
    script {
        pipelineParams.port = sh(script:'''grep EXPOSE Dockerfile | cut -d' ' -f2 ''', returnStdout:true).trim()
    }
}
