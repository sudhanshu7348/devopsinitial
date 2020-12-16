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
        
        parameters {
            string(name: 'APP_BRANCH', defaultValue: 'development', description: 'Which Branch should I build')
            choice(name: 'BUILD_TYPE', choices: 'non-production\nproduction', description: 'Choose either one of the options')
        }

        stages {
            stage('Build Info Set') {
                steps {
                    script {
                        def triggeredByUser = "${currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause).getUserName()}"
                        currentBuild.displayName = "${BUILD_DISPLAY_NAME} - ${APP_BRANCH}-${BUILD_TYPE} - ${triggeredByUser}"
                        pipelineParams.dockerRepo = BUILD_TYPE.equals('production') ? env.dockerProdRepo : env.dockerPreProdRepo
                        pipelineParams.appBranch = APP_BRANCH
                    } 
                }
            }
            stage('Checkout Project') { 
                steps {
                    git branch: APP_BRANCH, credentialsId: env.credentialsId, url: pipelineParams.gitUrl
                }
            }    
            stage('Get Application Info - Gradle') {
                when { equals expected: 'GRADLE', actual: pipelineParams.appType?.toUpperCase() }
                steps { gradleAppInfo(pipelineParams) }
            }
            stage('Get Application Info - Maven') {
                when { equals expected: 'MAVEN', actual: pipelineParams.appType.toUpperCase() }
                steps {
                    script {
                        pipelineParams.projectVersion = readMavenPom().getVersion() 
                        pipelineParams.artifactName = readMavenPom().getArtifactId()
                    }
                }
            }
            stage('Get Application Info - Node') {
                when { equals expected: 'NODE', actual: pipelineParams.appType.toUpperCase() }
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
            stage('Build Docker Image') { steps {  buildImage(pipelineParams) } }
            //stage('test') {}
            stage('Sonar Scan') { steps { sonarScan(pipelineParams) } }
            stage('Push Image') { steps { pushImage(pipelineParams) } }
            stage('Deploy Stack') {
                when { equals expected: 'non-production', actual: BUILD_TYPE }
                agent { label 'swarm_manager' }
                steps {
                    git branch: APP_BRANCH, credentialsId: env.credentialsId, url: pipelineParams.gitUrl
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