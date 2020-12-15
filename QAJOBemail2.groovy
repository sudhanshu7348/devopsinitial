def call(){
   //   sh 'printenv'
   
            env.mergedCommitVar=sh (
                     script: 'git log --oneline -1 ${GIT_COMMIT}',
                     returnStdout: true
                     ).trim()
                     
            env.commitedByVar=sh(
                   script: 'git log --format=medium -1 ${GIT_COMMIT} | head -n3 | tail -n2 | head -n1',
                   returnStdout: true
                   ).trim()
                   
            env.commitMeassageVar=sh(
                   script: 'git log --format=medium -1 ${GIT_COMMIT} | tail -n 1',
                   returnStdout: true
                   ).trim()
   
   
        def targetMailId
       def bodyTemplate = """
       <p style="color:blue;font-size:30px;font-weight:bold">Jenkins Build Finished Successfully</p>
       <b>PROJECT_NAME</b>:${PROJECT_NAME}
       <br/>   
       <b>BRANCH_NAME</b>:${BRANCH_NAME}
       <br/>
       <b>BUILD NUMBER</b>:${BUILD_NUMBER}
       <br/>
       <b>COMMIT_BY</b>:${commitedByVar}
       <br>
       <b>COMMIT_MESSGAGE</b>:${commitMeassageVar}
       <br>
       <b>COMMIT_MERGED</b>:${mergedCommitVar}
       <br>
       <b>JOB URL</b>:${BUILD_URL}
       <br>
       <p style="color:red"><i>**Please find logs in attachments</i></p>
        """
    /*   
        echo STAGE_NAME
   
   switch(STAGE_NAME) {
        case "SonarScan":
            targetMailId = 'Amit.Singh4@ge.com'
           break;
         case "UnitTesting":
            targetMailId = 'b@ge.com'
           break;
          case "kubernetesClusterDeployment":
             targetMailId = 'b@ge.com'
           break;     
         default:
            targetMailId = '@cg.com'
            }
   
  */ 
   
   echo targetMailId
   
       emailext attachLog: true, body: bodyTemplate, compressLog: true, subject: 'Jenkins Build Completed', to: 'cdx_projectc_testing_team@ge.com,Ashutosh.Pattanaik@ge.com', from: 'JenkinsAlert@ge.com'
}
