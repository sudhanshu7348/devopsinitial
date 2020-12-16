#!groovy
def call(Map pipelineParams) {
    script {
        pipelineParams.projectVersion = sh(script:'''node -pe "require('./package.json').version"''', returnStdout:true).trim() 
        pipelineParams.artifactName = sh(script:'''node -pe "require('./package.json').name"''', returnStdout:true).trim()
    }
}
