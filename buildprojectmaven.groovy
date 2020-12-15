def call(){
  mvnHome=tool 'maven'
  
  sh 'echo ${mvnHome}' 
  sh "'${mvnHome}/bin/mvn' package -Dmaven.test.skip=true"
  
  if(env.NEWRELIC_APP_NAME != "" && env.NEWRELIC_APP_NAME != null && env.NEWRELIC_APP_NAME != "null"){
    dir("${WORKSPACE}/environment/newrelic/") {
       fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}/target/")])
    }
    sh '''
       eval "( envsubst < ./target/newrelic-template.yml ) > ./target/newrelic.yml"
    '''
    env.JAVA_OPTS = env.NEWRELIC_PATH
  }
}
