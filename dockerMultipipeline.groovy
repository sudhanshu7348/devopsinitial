#!groovy
def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent { label 'swarm_worker' }

        environment { artifactoryCreds = credentials("${env.artifactoryId}") }
        
        options { timestamps() }
        
        stages {
            stage('Build Info Set') {
                steps {
                    script {
                        pipelineParams.appBranch = GIT_BRANCH
                        pipelineParams.dockerRepo = GIT_BRANCH.equals('master') || GIT_BRANCH.equals('release') 
                            ? env.dockerProdRepo 
                            : env.dockerPreProdRepo
                    } 
                }
            }
            stage('Get Application Info - Gradle') {
                when { equals expected: 'GRADLE', actual: pipelineParams.appType?.toUpperCase() }
                steps { gradleAppInfo(pipelineParams) }
            }
            stage('Get Application Info - Maven') {
                when { equals expected: 'MAVEN', actual: pipelineParams.appType?.toUpperCase() }
                steps { mavenAppInfo(pipelineParams) }
            }
            stage('Get Application Info - Node') {
                when { equals expected: 'NODE', actual: pipelineParams.appType?.toUpperCase() }
                agent { 
                    docker { 
                        image 'hc-us-east-aws-artifactory.cloud.health.ge.com/docker-brilliant-factory-all/node:9-slim' 
                        label 'swarm_worker' 
                        reuseNode true
                    } 
                }
                steps { nodeAppInfo(pipelineParams) }
            }
            stage('Get Port Number') {
                when { equals expected: null, actual: pipelineParams.port }
                steps { dockerFilePortNumber(pipelineParams) }
            }
            stage('Get Application Info - Validation') { steps { validateAppInfo(pipelineParams) } }
            stage('Build Docker Image') { steps { buildImage(pipelineParams) } }
            //stage('test') {}
            stage('Sonar Scan') { steps { sonarScan(pipelineParams) } }
            stage('Push Image') { steps { pushImage(pipelineParams) } }
            stage('Deploy Stack') {
                when {
                    not { branch 'release' }
                    //not { branch 'master' }
                }
                agent { label 'swarm_manager' }
                steps {
                    sh  """
                        #!/bin/bash
                        ${buildExports(pipelineParams)}
                        docker stack deploy -c ${pipelineParams.stackFile} ${pipelineParams.stackName}
                        """
                }
            }
        }
        post { always { deleteDir() } }
    }
}
