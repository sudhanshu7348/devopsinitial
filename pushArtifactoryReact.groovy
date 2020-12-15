def call(){

/*  sh ''' 
          mv ${WORKSPACE}/android/app/build/outputs/apk/release/*.apk ${WORKSPACE}/android/app/build/outputs/apk/release/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk
          sleep 20
          curl -H 'X-JFrog-Art-Api: AKCp5cbwSvMxtT2h7WvyRXVkEMqShVEK4D9K5SNJAanjpYbJtMY7yGh' -T ${WORKSPACE}/android/app/build/outputs/apk/release/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk  "https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk"
  '''		
  */
          archiveArtifacts artifacts: 'android/app/build/outputs/apk/release/*.apk'
          //archiveArtifacts 'android/app/build/outputs/apk/debug/*.apk'

}
