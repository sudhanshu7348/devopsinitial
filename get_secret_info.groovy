#!groovy
def call() {
    dir('secrets') {
        git branch: 'master', credentialsId: env.credentialsId, url: 'https://github.com/DET-Software-Cloud-Ops/Swarm-Secrets.git'
        sh  "chmod 400 aws/stratos-us-1-prod/ec2/${env.pemFileName}.pem"
    }
}
