def call(){

  sh ''' 
          mv ${WORKSPACE}/android/app/build/outputs/apk/debug/*.apk ${WORKSPACE}/android/app/build/outputs/apk/debug/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk
          sleep 20
          curl -H 'X-JFrog-Art-Api: AKCp5cbwSvMxtT2h7WvyRXK5SNJAanjpYbJtMY7yGh' -T ${WORKSPACE}/android/app/build/outputs/apk/debug/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk  "https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx/MygehcReact/QA/${PROJECT_NAME}-${PROJECT_VERSION}-${BRANCH_NAME}.apk"
  '''		
          //archiveArtifacts artifacts: '${WORKSPACE}/android/app/build/outputs/apk/debug/*.apk'
          //archiveArtifacts 'android/app/build/outputs/apk/debug/*.apk'

}
