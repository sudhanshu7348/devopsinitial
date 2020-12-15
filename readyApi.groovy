   def call(){
   
   env.GIT_URL=sh (
                  script: '''
                  git remote -v | awk '{print $2}' | head -n 1
                  ''',   
                  returnStdout: true
                  ).trim()
            build job: 'ReadyApiJob', parameters: [string(name: 'GitUrl', value: env.GIT_URL), string(name: 'ProjectScanned', value: env.PROJECT_NAME)]
            
            }
            
