/*
def creatingEnv(){
env.variable=function1.getArtifactoryName("master")
echo "***************called from function 3********************"
echo "function3" + env.variable
}
*/

def isDeployable(branch) {
  if ((branch.toLowerCase().equals("master") || branch.toLowerCase().equals("develop") //|| branch.toLowerCase().equals("jenkins-test")
  || branch.toLowerCase().equals("qa")|| branch.toLowerCase().equals("release")|| branch.toLowerCase().equals("pen1cx")
  || branch.toLowerCase().equals("depentest"))
  && env.AUTO_DEPLOYMENT  == "enabled") {
  return true
  }
  return false
}

