
         def call(projectName){
          checkout([$class: 'GitSCM', 
            branches: [[name: '*/master']], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', 
            relativeTargetDir: 'environment']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[url: 'https://github.build.ge.com/GSIT/cdx-environment.git',credentialsId: 'CDX_GIT_USER']]])
          
          env.PROJECT_VERSION = getPackageJson(env.WORKSPACE).version
          env.PROJECT_NAME = projectName ? projectName : getPackageJson(env.WORKSPACE).name.toLowerCase()
                  
          dir("${WORKSPACE}/environment/${PROJECT_NAME}/common/") {
            fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}")])
          }
                  
          sh 'printenv'
         }

        
