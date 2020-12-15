//
def call(){

pipeline{
agent any
stages{
stage('stage A'){
steps{
echo "In stage A"
script{
function1.getArtifactoryName("master")
sh 'printenv'
}
}
}
}
}


}
