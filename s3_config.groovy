def call(Map<String, Object> body) {
    pipeline {
        agent { label 'jump_box' }
        stages {
            stage('Get Secrets') {
                steps {
                    dir('secrets') {
                        git branch: 'master', credentialsId: env.credentialsId, url: 'https://github.build.ge.com/DET-Software-Cloud-Ops/Swarm-Secrets.git'
                        sh  "chmod 400 aws/stratos-us-1-prod/ec2/${env.pemFileName}.pem"
                    }
                }
            }
            stage('Configure Blobstore access in Jumpbox') { 
                steps {
                    dir('project') {
                        sh  """
                            eval \"\$(ssh-agent)\"
                            ssh-add ../secrets/aws/stratos-us-1-prod/ec2/${env.pemFileName}.pem

ssh ubuntu@${env.prodSwarm} <<- 'EOF'
    aws configure set aws_access_key_id ${body.PREDIX_ACCESS_KEY} --profile predix
    aws configure set aws_secret_access_key ${body.PREDIX_SECRET} --profile predix
    aws configure set region ${body.REGION} --profile predix
    mkdir -p ./${body.PREDIX_BUCKET_GUID}
    aws s3 sync s3://${body.PREDIX_BUCKET_GUID} ./${body.PREDIX_BUCKET_GUID} --profile predix
    wait
    aws s3 sync ./${body.PREDIX_BUCKET_GUID} s3://${body.AWS_S3_BUCKET} 
    wait
    rm -rf ./${body.PREDIX_BUCKET_GUID}
EOF
                            """
                    }
                }
            }
            
        }
        post {
            always {
                deleteDir()
            }
        }
    }
}
