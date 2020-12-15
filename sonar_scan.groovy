def call(){
  
   if(BRANCH_NAME.equals("develop") || BRANCH_NAME.matches("PR-.*"))
   {
 //Central Sonar Scan
    // build job: 'central-sonar-scan', parameters: [string(name: 'GIT_URL', value: env.GIT_URL), string(name: 'BRANCH_NAME', value: env.BRANCH_NAME)]
 scannerHome=tool 'sonar'
 '''    
  withSonarQubeEnv('sonar-central') {
   //sh "'${mvnHome}/bin/mvn' clean package -Dmaven.test.skip=true"
   sh "${scannerHome}/bin/sonar-scanner -Dsonar.host.url=https://sonar.cloud.health.ge.com -Dsonar.c.file.suffixes=- -Dsonar.cpp.file.suffixes=- -Dsonar.objc.file.suffixes=-"
                                    }
 
 ''' 
 withSonarQubeEnv('sonar-cdx') {
   //sh "'${mvnHome}/bin/mvn' clean package -Dmaven.test.skip=true"
   sh "${scannerHome}/bin/sonar-scanner -Dsonar.host.url=http://cdx-sonarqube.cloud.health.ge.com/"
     }

   }
  
  
   else
   {
   sh '''
   echo "Skipping Sonar Scan"
   '''
   }

   // echo "Skipping Sonar scan due to ongoing maintenance. The scan will be enabled once the service is up and running. Thank you.\n"
   
}
