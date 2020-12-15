def call(){
  sh '''
      npm install
      ng build --configuration=${APP_ACTIVE_PROFILE}
  '''
}

//npm install -g @angular/cli@latest
