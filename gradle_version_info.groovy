#!groovy
def call(Map pipelineParams) {
    script {
        pipelineParams.projectVersion = sh(script: 
            '''
            chmod +x gradlew
            ./gradlew properties -q | grep \"version:\" | awk '{print \$2}' 
            ''', returnStdout:true).trim()
    }
}
