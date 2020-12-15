def call(){
  
  if(BRANCH_NAME.matches("PR-.*") || BRANCH_NAME.equals("develop"))
  {
  mvnHome=tool 'maven'
  sh 'echo ${mvnHome}'
  //sh "'${mvnHome}/bin/mvn' -X clean install -Dmaven.test.failure.ignore=true"
 
  sh "'${mvnHome}/bin/mvn' clean test -Dmaven.test.failure.ignore=true"
 // sh "'${mvnHome}/bin/mvn' verify -Dmaven.test.failure.ignore=true"
  
 // junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
 // archive 'target/*.jar'
  }
  else
  {
  sh '''
  echo "Skipping Unit Test"
  '''
  }
}
