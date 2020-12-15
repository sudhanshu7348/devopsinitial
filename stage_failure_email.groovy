def call(){
   //   sh 'printenv'
   
   if(BRANCH_NAME.matches("PR-.*")){
   
   
   def targetMailId
   
   
   def bodyTemplate = """
       <p style="color:red;font-size:30px;font-weight:bold">JENKINS STAGE FAILURE</p>
       <b>FAILED_STAGE</b>: ${STAGE_NAME}
       <br/>
       <b>PROJECT_NAME</b>:${PROJECT_NAME}
       <br/>   
       <b>BRANCH_NAME</b>:${BRANCH_NAME}
        <br/>
       <b>BUILD NUMBER</b>:${BUILD_NUMBER}
        <br/>
        <b>GIT_COMMIT</b>:${GIT_COMMIT}
        <br/>
        <b>JOB_URL</b>:${BUILD_URL}
        <br>
        <p style="color:red"><i>**Please find logs in attachments</i></p>
        """
       
        echo STAGE_NAME
   
   switch(STAGE_NAME) {
        case "SonarScan":
            targetMailId = 'cdx_projectc_dev_team@ge.com'
           break;
         case "UnitTesting":
            targetMailId = 'cdx_projectc_dev_team@ge.com'
           break;
         case "MavenBuild":
            targetMailId= 'cdx_projectc_dev_team@ge.com'
            break;
         case "ImageBuild&Push":
            targetMailId= 'CDX_AutomationTeam@ge.com'
            break;
         case "KubernetesDeployment":
            targetMailId= 'CDX_AutomationTeam@ge.com'
            break;
         default:
            targetMailId = 'CDX_AutomationTeam@ge.com'
            }
    
   echo targetMailId
   
       emailext attachLog: true, body: bodyTemplate, compressLog: true, subject: 'Jenkins Pipeline Failure', to: targetMailId, from: 'JenkinsAlert@ge.com'
            }

   
   else
   {
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
       <p style="color:red;font-size:30px;font-weight:bold">JENKINS STAGE FAILURE</p>
       <b>FAILED_STAGE</b>: ${STAGE_NAME}
       <br/>
       <b>PROJECT_NAME</b>:${PROJECT_NAME}
       <br/>   
       <b>BRANCH_NAME</b>:${BRANCH_NAME}
        <br/>
       <b>BUILD NUMBER</b>:${BUILD_NUMBER}
        <br/>
        <b>GIT_COMMIT</b>:${GIT_COMMIT}
        <br/>
        <b>COMMIT_BY</b>:${commitedByVar}
        <br>
        <b>COMMIT_MESSGAGE</b>:${commitMeassageVar}
        <br>
        <b>COMMIT_MERGED</b>:${mergedCommitVar}
        <br>
        <b>JOB_URL</b>:${BUILD_URL}
        <br>
        <p style="color:red"><i>**Please find logs in attachments</i></p>
        """
       
        echo STAGE_NAME
   
   switch(STAGE_NAME) {
        case "SonarScan":
            targetMailId = 'cdx_projectc_dev_team@ge.com'
           break;
         case "UnitTesting":
            targetMailId = 'cdx_projectc_dev_team@ge.com'
           break;
         case "MavenBuild":
            targetMailId= 'cdx_projectc_dev_team@ge.com'
            break;
         case "ImageBuild&Push":
            targetMailId= 'CDX_AutomationTeam@ge.com'
            break;
         case "KubernetesDeployment":
            targetMailId= 'CDX_AutomationTeam@ge.com'
            break;
         default:
            targetMailId = 'CDX_AutomationTeam@ge.com'
            }
    
   echo targetMailId
   
       emailext attachLog: true, body: bodyTemplate, compressLog: true, subject: 'Jenkins Pipeline Failure', to: targetMailId, from: 'JenkinsAlert@ge.com'
            }
   }
