import groovy.json.JsonSlurper

def call(){
    def config
    def envConfig
    try {
        config=new File(env.workspace+'/coverityConfig-template.json').text
        envConfig=new JsonSlurper().parseText(config)
        return envConfig.coverityParams
    } catch(Exception e){
        return new JsonSlurper().parseText("{}")
    }
}
