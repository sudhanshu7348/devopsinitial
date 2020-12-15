def call(){

sh label: '', script: '''
ANDROID_HOME=/var/lib/jenkins/android-sdk
export PATH="$ANDROID_HOME:$PATH"
export PATH="$ANDROID_HOME/tools:$PATH"
export PATH="$ANDROID_HOME/tools/bin:$PATH"
export PATH="$ANDROID_HOME/build-tools:$PATH"
export PATH="$ANDROID_HOME/platform-tools:$PATH"


rm -rf node_modules
npm i -f
npm test
npm uninstall -g cordova
npm install -g ionic cordova

ionic cordova platform remove android@7.0.0 --save
ionic cordova platform add android@7.0.0 --save
ionic cordova plugin add cordova-android-support-gradle-release --fetch
ionic cordova build android'''
}
