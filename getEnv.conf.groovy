import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

def getEnvConfig(branch) {
def k8Config=new File(env.workspace+'/kubeConfig.json').text
def envConfig = new JsonSlurper().parseText(k8Config)

if (envConfig[branch] != null){
    return envConfig[branch]
  } /*else if (branch.toLowerCase().equals("jenkins-test")){
    return envConfig.master
  }*/
    return envConfig.default
}
