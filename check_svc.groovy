#!groovy
def call(Map<String, Object> body) {
    pipeline {
        agent { label 'jump_box' }
        stages {
            stage('Get Secrets') { steps { getSecrets() } }  
            stage('Check Service') { 
                steps {
                    dir('project') {
                        sh  """
                            eval \"\$(ssh-agent)\"
                            ssh-add ../secrets/aws/stratos-us-1-prod/ec2/${env.pemFileName}.pem
                            docker -H ssh://ubuntu@${env.prodSwarm} service ls -f name=${body.serviceName} --format "Service: {{.Name}}\n  Replicas: {{.Replicas}}\n-----\n"
                            docker -H ssh://ubuntu@${env.prodSwarm} service ps --no-trunc --format "Task: {{.Name}}\n  Image: {{.Image}}\n  State: {{.CurrentState}}\n  Error: {{.Error}}\n-----\n" ${body.serviceName}
                            ssh-agent -k
                            """
                    }
                }
            }
        }
        post { always { deleteDir() } }
    }
}
