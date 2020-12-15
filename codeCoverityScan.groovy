def call(){
	
if(BRANCH_NAME.equals("qa") || BRANCH_NAME.equals("release") ){
	
	      env.projectName = readCoverityConfigFile().projectName
          env.streamName = readCoverityConfigFile().streamName
          env.viewName = readCoverityConfigFile().viewName
          env.coverityServer = readCoverityConfigFile().coverityServer

   def mvnHome=tool 'maven'
   sh '''
   eval "( envsubst < coverityConfig-template.json ) > coverityConfig.json"
   '''
    withCoverityEnvironment(coverityInstanceUrl: "${coverityServer}", projectName: "${PROJECT_NAME}", streamName: "${streamName}", viewName: "${viewName}") 
    {
       //sh "cov-build --dir coverityResults ${mvnHome}/bin/mvn clean package"
       sh "cov-build --dir ${WORKSPACE}/idir mvn -Dmaven.test.skip=true clean install"
       sh "cov-analyze --dir ${WORKSPACE}/idir -webapp-security --webapp-security-preview --trust-network --trust-servlet --security --enable-fb --skip-webapp-sanity-check"
       //sh "cov-analyze --dir coverityResults"
      withCredentials([file(credentialsId: 'CoverityAuth', variable: 'COV_AUTH_KEY_FILE')]) 
      {
          //sh "cov-commit-defects --dir coverityResults  --auth-key-file ${COV_AUTH_KEY_FILE} --host coverity.cloud.health.ge.com --port 8080 --stream ${streamName}"
         sh "cov-commit-defects --dir ${WORKSPACE}/idir --host coverity.cloud.health.ge.com --port 8080 --stream ${streamName}"
      }
    

}
                                        }
	
																	
else
	{
    	sh '''
		  echo "Branch Not found hence scan stopped "
		'''
		}
	
	
	
}
