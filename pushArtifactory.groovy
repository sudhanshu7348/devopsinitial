def call(){

  sh ''' 
          mv ${WORKSPACE}/platforms/android/app/build/outputs/apk/debug/*.apk ${WORKSPACE}/platforms/android/app/build/outputs/apk/debug/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk
          sleep 20
          curl -H 'X-JFrog-Art-Api: AKCp5cbwSvMxtT2h7WvyRXVtGTdN5SNJAanjpYbJtMY7yGh' -T ${WORKSPACE}/platforms/android/app/build/outputs/apk/debug/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk  "https://hc-us-east-aws-artifactory.cloud.health.ge.com/generic-cdx-all/${BRANCH_NAME}/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk"
  '''		
          archiveArtifacts 'platforms/android/app/build/outputs/apk/debug/*.apk'


}
