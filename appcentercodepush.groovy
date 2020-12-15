def call(){

  sh '''
npm i -f
npm install -g appcenter-cli
appcenter login --token dccc2cc38dd6a7474
appcenter codepush release-react -a cd-g/MyGEHC-Android -d Stage -m true
  '''
}
