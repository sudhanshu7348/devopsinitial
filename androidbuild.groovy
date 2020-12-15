def call(){

nodeHome=tool 'node_mygehc_develop'
  
  sh 'echo ${nodeHome}' 
  

        
sh label: '', script: '''
ANDROID_HOME=/var/lib/jenkins/android-sdk
export PATH="$ANDROID_HOME:$PATH"
export PATH="$ANDROID_HOME/tools:$PATH"
export PATH="$ANDROID_HOME/tools/bin:$PATH"
export PATH="$ANDROID_HOME/build-tools:$PATH"
export PATH="$ANDROID_HOME/platform-tools:$PATH"


printenv
/var/lib/jenkins/tools/jenkins.plugins.nodejs.tools.NodeJSInstallation/node_mygehc_develop/bin/npm install || true
/var/lib/jenkins/tools/jenkins.plugins.nodejs.tools.NodeJSInstallation/node_mygehc_develop/bin/npm install -g react-native-cli

'''
switch(BRANCH_NAME){
case "htm":
case "PR-*":
case "dev":
sh 'ENVFILE=.env npm run dev-release-apk'
break;
case "qa":
sh 'ENVFILE=.env.qa npm run qa-release-apk'
break;
case "release":
sh 'ENVFILE=.env.staging npm run stage-release-apk'
break;
case "master":
sh 'ENVFILE=.env.prod npm run prod-release-apk'
break;
default:
sh 'ENVFILE=.env npm run dev-release-apk'
}

//sh 'echo skipping compilation'
     //   sh "'${nodeHome}/bin/mvn' package -Dmaven.test.skip=true"

        }
