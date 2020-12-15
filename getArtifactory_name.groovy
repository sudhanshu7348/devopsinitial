/*
#!/usr/bin/env groovy
def call(String Name=human) { 
pipeline{
agent any
stages{
stage('stage 1'){
steps{
echo "${Name}"
  echo "In build class"
      }
              }
   }
}
  //Method code 
  echo "Call"
}
*/



//import groovy.json.JsonSlurper
//import groovy.json.JsonBuilder

def getArtifactoryName(branch) {
  if (branch.toLowerCase().equals("master")) {
  //  echo "cdxRelease"
    return 'docker-cdx-release'
  } else {
    //echo "cdx-snapshot"
    return 'docker-cdx-snapshot'
}
                                }




