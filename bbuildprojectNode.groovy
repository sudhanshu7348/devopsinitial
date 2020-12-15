def call(){
  sh '''
    if [ $IS_DEPLOYABLE == "false" ]; then
      npm run compile
    fi
  '''
}
