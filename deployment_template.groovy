#!groovy
def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent { label 'jump_box' }
        stages {
            stage('Get Secrets') { steps { getSecrets() } }
            stage('Checkout Project') { 
                steps {
                    dir('project') {
                        git branch: pipelineParams.projectBranch, credentialsId: env.credentialsId, url: pipelineParams.gitUrl
                    }
                }
            }    
            stage('Get Application Info - Gradle') {
                when { equals expected: 'GRADLE', actual: pipelineParams.appType?.toUpperCase() }
                steps { dir('project') { gradleAppInfo(pipelineParams) } }
            }
            stage('Get Application Info - Maven') {
                when { equals expected: 'MAVEN', actual: pipelineParams.appType?.toUpperCase() }
                steps { dir('project') { mavenAppInfo(pipelineParams) } }
            }
            stage('Get Application Info - Node') {
                when { equals expected: 'NODE', actual: pipelineParams.appType?.toUpperCase() }
                agent {
                    docker {
                        image 'node:latest'
                        label 'jump_box'
                        reuseNode true
                    } 
                }
                steps { dir('project') { nodeAppInfo(pipelineParams) } }
            }
            stage('Get Port Number') {
                when { expression { pipelineParams.appType != null && pipelineParams.port == null } }
                steps { dir('project') { dockerFilePortNumber(pipelineParams) } }
            }
            stage('Get Application Info - Validation') { steps { validateAppInfo(pipelineParams) } }
            stage('Deploy Stack') { 
                steps {
                    dir('project') {
                        sh  """
                            eval \"\$(ssh-agent)\"
                            ssh-add ../secrets/aws/stratos-us-1-prod/ec2/${env.pemFileName}.pem
                            ${buildExports(pipelineParams)}
                            docker -H ssh://ubuntu@${env.prodSwarm} stack deploy -c ${pipelineParams.stackFileName} ${pipelineParams.stackName}
                            ssh-agent -k
                            """
                    }
                }
            }
        }
        post { always { deleteDir() } }
    }
}
