#!groovy
def image_tag ='0.0.0'
def deployment_namespace='dev'
def app_url=''
def logstream='error'
def helm_deploy_name=''
def helm_deploy_to_delete=''
def branch_to_delete=''
def additional_helm_args=''

def prod_versions_patterns="'v?.?.?' 'v?.?.??' 'v?.?.???' " +
    "'v?.??.?' 'v?.??.??' 'v?.??.???' " + 
    "'v?.???.?' 'v?.???.??' 'v?.???.???' " + 
    "'v??.?.?' 'v??.?.??' 'v??.?.???' " +
    "'v??.??.?' 'v??.??.??' 'v??.??.???' " + 
    "'v??.???.?' 'v??.???.??' 'v??.???.???' " +
    "'v???.?.?' 'v???.?.??' 'v???.?.???' " +
    "'v???.??.?' 'v???.??.??' 'v???.??.???' " + 
    "'v???.???.?' 'v???.???.??' 'v???.???.???'"
def SEMVER_REGEX=/v([0-9]*).([0-9]*).([0-9]*)-?([0-9A-Za-z-.]+)?/
def PATCHVER_REGEX=/patch-([0-9]*).([0-9]*)/
def BRANCH_REGEX=/\/([^\/]*)$/

// App URL Scheme:
// feature branch minor-us*-f*.dev.indexer.odp-daas.cloud.health.ge.com
// patch brach patch-us*-f*.stg.indexer.odp-daas.cloud.health.ge.com
// Dev branch stg.indexer.odp-daas.cloud.health.ge.com
// master branch indexer.odp-daas.cloud.health.ge.com

def getRepoURL() {
  sh "git config --get remote.origin.url > .git/remote-url"
  return readFile(".git/remote-url").trim()
}

def getCommitSha() {
  sh "git rev-parse HEAD > .git/current-commit"
  return readFile(".git/current-commit").trim()
}

def updateGithubCommitStatus(String message, String state) {
  // workaround https://issues.jenkins-ci.org/browse/JENKINS-38674
  repoUrl = getRepoURL()
  commitSha = getCommitSha()

  step([
    $class: 'GitHubCommitStatusSetter',
    reposSource: [$class: "ManuallyEnteredRepositorySource", url: repoUrl],
    commitShaSource: [$class: "ManuallyEnteredShaSource", sha: commitSha],
    errorHandlers: [[$class: 'ShallowAnyErrorHandler']],
    statusResultSource: [
      $class: 'ConditionalStatusResultSource',
      results: [
        [$class: 'AnyBuildResult', state: state, message: message]
      ]
    ]
  ])
}

pipeline {
    
    agent any
    tools { 
        maven 'mvn'
        'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'Docker'
    }
    
    environment {
        // Change by repo:
        app_short_name="template"

        DOCKER_OPTS="-H tcp://0.0.0.0:2376 -H unix:///var/run/docker.sock"
        image_repository="541574621075.dkr.ecr.us-east-1.amazonaws.com/daas" 
        app_name="odp-daas-${app_short_name}"
        cluster_name="odp-us-innovation-eks-poc"
        region="us-east-1"
        ingress_url='internal-a0e09834775c511eaadbd0af8f74500b-200432936.us-east-1.elb.amazonaws.com'
    }
    
    stages {
        stage ('Initialize') {
            steps {
                updateGithubCommitStatus("Build Started", "PENDING");
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    aws eks update-kubeconfig --name ${cluster_name} --region ${region}
                ''' 
            }
        }
        stage ('Dev Versioning') {
            when {
                branch 'dev'
            } 
            steps {
                script {
                    logstream = 'stg'

                    // Get most recent version number from the git tag history
                    def currentVersion = sh (
                        script: 'git tag --sort=v:refname -l "v*-dev" | tail -1',
                        returnStdout: true
                    ).trim()
                    def versionBreakdown = (currentVersion =~ SEMVER_REGEX)[ 0 ]
                    def major = versionBreakdown[ 1 ].toInteger()
                    def minor = versionBreakdown[ 2 ].toInteger()
                    def defect = versionBreakdown[ 3 ].toInteger()
    
                    // Try to get the source branch from the parent commit
                    def sourceInfo = sh (
                        script: "git show --no-patch --pretty=format:%D \$(git show --no-patch --pretty=format:%p | cut -d ' ' -f2)",
                        returnStdout: true
                    ).trim()

                    // Set a default value so don't fail if can't find source branch. If no source branch found will be treated as defect fix.
                    def sourceBranch = "Not Found";

                    if ((sourceInfo =~ BRANCH_REGEX).getCount() > 0) {
                        sourceBranch = (sourceInfo =~ BRANCH_REGEX)[0][1]
                    } else {
                       // If can't get source branch from parent commit, try to read it from the commit comment
                       sourceInfo = sh (
                            script: "git show --oneline",
                            returnStdout: true
                        ).trim()
                        
                        if ((sourceInfo =~ BRANCH_REGEX).getCount() > 0) {
                           sourceBranch = (sourceInfo =~ BRANCH_REGEX)[0][1] 
                        }
                    }

                    echo("${currentVersion}")
                    echo("${major}")
                    echo("${minor}")
                    echo("${defect}")
                    echo("${sourceBranch}")


                    // Get the sha of both the current commit and the commit tagged with the current version
                    // This is used to ensure that pipeline doesn't fail if we re-run on a tagged commit
                    def currentCommitSHA = sh (
                        script: 'git show --no-patch --pretty=format:%H',
                        returnStdout: true
                    ).trim()

                    def currentVersionSHA = sh (
                        script: "git show --no-patch --pretty=format:%H ${currentVersion}",
                        returnStdout: true
                    ).trim()

                    // If the current commit is the currently tagged version, don't increment the version number
                    if (!currentCommitSHA.equals(currentVersionSHA)) {
                        if (sourceBranch ==~ /major_.*/) {
                            major++
                            minor = 0
                            defect = 0
                        } else if (sourceBranch ==~ /minor_.*/) {
                            minor++
                            defect = 0
                        } else {
                            defect++
                        }
                    }

                    image_tag="v${major}.${minor}.${defect}-dev"

                    // If the current commit is the currently tagged version, don't re-tag it (would cause error)
                    if (!currentCommitSHA.equals(currentVersionSHA)) { sh "git tag -f ${image_tag}" }

                    sh "sed -i \"s/appVersion.*\$/appVersion: ${image_tag}/\" ./${app_name}-chart/Chart.yaml"

                    echo (" Image Tag = ${image_tag} " )
                    deployment_namespace="stg"

                    helm_deploy_name="${app_name}".replace("_", "-").replace(".","-").toLowerCase()

                    helm_deploy_to_delete="${sourceBranch}".replace("_", "-").replace(".","-").toLowerCase()
                    branch_to_delete="${sourceBranch}"

                    app_url="${deployment_namespace}.${app_short_name}.odp-daas.cloud.health.ge.com"

                    additional_helm_args = "--atomic"
                }
                sshagent (credentials: ['git']) {
                    sh('git push origin --tags || true')
                }
            }
        }
        stage ('Patch Versioning') {
            when {
                branch 'patch_*'
            } 
            steps {
                script {
                    branch = "${GIT_BRANCH}"

                    // Get most recent production version number from the git tag history
                    def currentProdVersion = sh (
                        script: "git tag --sort=v:refname -l ${prod_versions_patterns} | tail -1",
                        returnStdout: true
                    ).trim()

                    image_tag="${currentProdVersion}-patch"

                    sh "sed -i \"s/appVersion.*\$/appVersion: ${image_tag}/\" ./${app_name}-chart/Chart.yaml"

                    echo ("Image Tag = ${image_tag} ")
                    deployment_namespace="stg"
                    logstream = branch.replace("*", "").replace(":", "")

                    helm_deploy_name="${branch}".replace("_", "-").replace(".","-").toLowerCase()

                    app_url="${helm_deploy_name}.${deployment_namespace}.${app_short_name}.odp-daas.cloud.health.ge.com"

                    additional_helm_args = "--force"
                }
                sshagent (credentials: ['git']) {
                    sh("git push --delete origin ${image_tag} || true")
                    sh "git tag -f ${image_tag}"
                    sh('git push origin --tags')
                }
            }
        }
        stage('Build') {
           when {
                anyOf{ branch 'major_*'; branch 'minor_*'; branch 'defect_*'; branch 'patch_*'; branch 'dev'}
            }  
            steps {
                sh 'mvn -B -DskipTests clean install -Dmaven.repo.local=/tmp/mvn_repository'
            }
        }
        stage ('Feature Versioning') {
            when {
                anyOf{ branch 'major_*'; branch 'minor_*'; branch 'defect_*'}
            } 
            steps {
                script {
                    def currentCommitShortSHA = sh (
                        script: 'git show --no-patch --pretty=format:%h',
                        returnStdout: true
                    ).trim()
                    branch = "${GIT_BRANCH}"
                    logstream = branch.replace("*", "").replace(":", "")

                    image_tag= branch + currentCommitShortSHA
                    
                    echo (" Image Tag = ${image_tag} ")
                    if (GIT_BRANCH ==~ /patch.*/) {
                        // deployment_namespace = "test"
                        deployment_namespace = "dev"
                    } else {
                        deployment_namespace="dev"
                    }
                    helm_deploy_name="${branch}".replace("_", "-").replace(".","-").toLowerCase()
                    app_url="${helm_deploy_name}.${deployment_namespace}.${app_short_name}.odp-daas.cloud.health.ge.com"

                    additional_helm_args = "--force"
                }
            }
        }

        stage('Deploy') {
            when {
                anyOf{ branch 'major_*'; branch 'minor_*'; branch 'defect_*'; branch 'patch_*'; branch 'dev'}
            } 
            steps {
                sh """
                    #!/bin/bash
                    docker build . -t ${image_repository}/${app_name}:${image_tag}
                """
                retry(5) {
                    sh "aws ecr get-login --no-include-email --region us-east-1|/bin/bash"
                }
                retry(5) {
                    sh "docker push ${image_repository}/${app_name}:${image_tag}"
                }
                retry (3) {
                    sh "helm upgrade --install ${additional_helm_args} -n ${deployment_namespace} ${helm_deploy_name} --set image.tag=${image_tag} --set ingress.hosts[0]=${app_url} --set extraEnv[0].name=logstream,extraEnv[0].value=${logstream} ./${app_name}-chart"
                }
                sh """
                    echo "Click the following link to create the DNS entry for your branch. Must be done once per branch name"
                    echo "http://automation.cloud.health.ge.com/createcname/${app_url}/${ingress_url}"
                    echo "Once the DNS name is created, you can access your app at the following link. It may take a few minutes to become active the first time"
                    echo "http://${app_url}"
                """
            }
        }
    }
    post { 
        always {
            script {
                if (helm_deploy_to_delete ==~ /((major)|(minor)|(defect)|(patch)).*/) {
                    sh "helm delete -n dev ${helm_deploy_to_delete} || true"
                }
            }
            sh 'docker image prune --all --force --filter "until=168h" || true'
        }
        success {
            updateGithubCommitStatus("Build succeeded", "SUCCESS");
            sshagent (credentials: ['git']) {
                script {
                    if (branch_to_delete ==~ /((major)|(minor)|(defect)|(patch)).*/) {
                        sh "git push origin --delete ${branch_to_delete} || true"
                    }
                }
            }
        }
        failure {
            updateGithubCommitStatus("Build failed", "FAILURE");
        }
    }
}
