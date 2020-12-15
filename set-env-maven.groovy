
         def call(projectName){
                  
         properties([[$class: 'JiraProjectProperty'],buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '1', numToKeepStr: '5'))])      
          
          checkout([$class: 'GitSCM', 
            branches: [[name: '*/master']], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', 
            relativeTargetDir: 'environment']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[url: 'https://github.build.ge.com/GSIT/cdx-environment.git',credentialsId: 'CDX_GIT_USER']]])
          
          env.POM_VER = readMavenPom file: 'pom.xml'
          env.PROJECT_VERSION = env.POM_VER.toString().split(':')[3]
          env.PROJECT_NAME = projectName ? projectName : env.POM_VER.toString().split(':')[1].toLowerCase()
          
          //using this to generalise dockerfile        
          env.DELIVERABLE_NAME = env.POM_VER.toString().split(':')[1]
                  
                  
          dir("${WORKSPACE}/environment/${PROJECT_NAME}/common/") {
            fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}")])
          }
             sh 'printenv'
        //  env.BRANCH_NAME = env.GIT_BRANCH.replaceAll("origin/", "")  
          env.ENV_CONFIG_DIR = getEnvConfig.getEnvConfig(env.BRANCH_NAME).configDir
          env.ARTIFACTORY_NAME = getArtifactoryName.getArtifactoryName(env.BRANCH_NAME)
          env.ARTIFACTORY_URL = 'hc-us-east-aws-artifactory.cloud.health.ge.com'
           // Repointing the EU builds to US artifactory as EU is down
          //env.EU_ARTIFACTORY_URL = 'hc-eu-west-aws-artifactory.cloud.health.ge.com'
          env.EU_ARTIFACTORY_URL = 'hc-us-east-aws-artifactory.cloud.health.ge.com'
          env.CREDENTIAL_ID = 'Artifactory'
          // Repointing the EU builds to US artifactory as EU is down
          // env.CREDENTIAL_ID_EU = 'EU_ARTIFACTORY'
          env.CREDENTIAL_ID_EU = 'Artifactory'
          env.AUTO_DEPLOYMENT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).autoDeployment ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).autoDeployment : 'enabled'
          env.IS_DEPLOYABLE = isDeployable.isDeployable(env.BRANCH_NAME)
          env.APP_ACTIVE_PROFILE = getEnvConfig.getEnvConfig(env.BRANCH_NAME).activeProfile
          env.NAMESPACE = getEnvConfig.getEnvConfig(env.BRANCH_NAME).namespace
          env.HOST_NAME = getEnvConfig.getEnvConfig(env.BRANCH_NAME).hostname
          env.CONTEXT_PATH = getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath: '/'
          env.CONTEXT_PATH_2 = getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath2 ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath2: '/'
          env.CONTEXT_PATH_3 = getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath3 ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath3: '/'
          env.CONTEXT_PATH_4 = getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath4 ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath4: '/'
          env.CONTEXT_PATH_5 = getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath5 ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).contextPath5: '/'
          //OPTIONAL -swagger host
          env.SWAGGER_HOST = getEnvConfig.getEnvConfig(env.BRANCH_NAME).swaggerhost
          env.MIN_REPLICAS = getEnvConfig.getEnvConfig(env.BRANCH_NAME).minReplicas
          env.MAX_REPLICAS = getEnvConfig.getEnvConfig(env.BRANCH_NAME).maxReplicas
          env.CPU = getEnvConfig.getEnvConfig(env.BRANCH_NAME).cpu
          env.MEMORY = getEnvConfig.getEnvConfig(env.BRANCH_NAME).memory
          env.CPU_LIMIT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).cpuLimit
          env.MEMORY_LIMIT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).memoryLimit
          env.TGT_CPU_UTIL_PERC =  getEnvConfig.getEnvConfig(env.BRANCH_NAME).targetCPUUtilizationPercentage
          env.PROJECT_AUTH_SVC =  getEnvConfig.getEnvConfig(env.BRANCH_NAME).authSvc
          env.DEPLOYMENT_REGION = getEnvConfig.getEnvConfig(env.BRANCH_NAME).deploymentRegion
          env.DEPLOYMENT_SERVER = getDeploymentServer.getDeploymentServer(env.BRANCH_NAME)
          env.APP_PORT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).APP_PORT ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).APP_PORT: '8080'
          env.DOLLAR='$'
          env.dockertarget= "${DELIVERABLE_NAME}-${PROJECT_VERSION}.jar"
          env.NEWRELIC_APP_NAME = getEnvConfig.getEnvConfig(env.BRANCH_NAME).newrelicAppName
          env.NEWRELIC_PATH = '-javaagent:/apps/newrelic.jar'
          env.VOLUME_MOUNT_PATH = getEnvConfig.getEnvConfig(env.BRANCH_NAME).volumeMountTarget
          env.BUILD_TIME = currentBuild.getTimeInMillis()
          env.MOUNT_PATH_PREFIX = getEnvConfig.getEnvConfig(env.BRANCH_NAME).mountPathPrefix
          env.SYNTHETICS_HOST = getEnvConfig.getEnvConfig(env.BRANCH_NAME).syntheticsHost
                  
        
         
          dir("${WORKSPACE}/environment/${PROJECT_NAME}/${ENV_CONFIG_DIR}/") {
            fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}/src/main/resources/")])
          }
          sh 'printenv'
        
         }
