
         def call(projectName){
          checkout([$class: 'GitSCM', 
            branches: [[name: '*/master']], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', 
            relativeTargetDir: 'environment']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[url: 'https://github.build.ge.com/GSIT/cdx-environment.git',credentialsId: 'CDX_GIT_USER']]])
          
          env.PROJECT_NAME = projectName ? projectName :getPackageJson(env.WORKSPACE).name.toLowerCase()
                  
          dir("${WORKSPACE}/environment/${PROJECT_NAME}/common/") {
            fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}")])
          }

          env.PROJECT_VERSION = getEnvConfig.getEnvConfig(env.BRANCH_NAME).versionSuffix? getPackageJson(env.WORKSPACE).version + getEnvConfig.getEnvConfig(env.BRANCH_NAME).versionSuffix : getPackageJson(env.WORKSPACE).version
        //  env.BRANCH_NAME = env.GIT_BRANCH ? env.GIT_BRANCH.replaceAll("origin/", "") : ""; 
          env.ENV_CONFIG_DIR = getEnvConfig.getEnvConfig(env.BRANCH_NAME).configDir
          env.ARTIFACTORY_NAME = getArtifactoryName.getArtifactoryName(env.BRANCH_NAME)
          env.ARTIFACTORY_URL = 'hc-us-east-aws-artifactory.cloud.health.ge.com'
          env.EU_ARTIFACTORY_URL = 'hc-us-east-aws-artifactory.cloud.health.ge.com'
          env.CREDENTIAL_ID = 'Artifactory'
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
          env.APP_PORT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).APP_PORT ? getEnvConfig.getEnvConfig(env.BRANCH_NAME).APP_PORT:'3000'
          env.DOLLAR='$'
          env.coverityProjectName = readCoverityConfigFile().projectName
          env.streamName = readCoverityConfigFile().streamName
          env.viewName = readCoverityConfigFile().viewName
          env.coverityServer = readCoverityConfigFile().coverityServer
          env.DEPLOYMENT_REGION = getEnvConfig.getEnvConfig(env.BRANCH_NAME).deploymentRegion
          env.DEPLOYMENT_SERVER = getDeploymentServer.getDeploymentServer(env.BRANCH_NAME)
          env.NEWRELIC_APP_NAME = getEnvConfig.getEnvConfig(env.BRANCH_NAME).newrelicAppName
          env.NEWRELIC_PATH = '-javaagent:/apps/newrelic.jar'
          env.VOLUME_MOUNT_PATH = getEnvConfig.getEnvConfig(env.BRANCH_NAME).volumeMountTarget
          env.BUILD_TIME = currentBuild.getTimeInMillis()
          env.MOUNT_PATH_PREFIX = getEnvConfig.getEnvConfig(env.BRANCH_NAME).mountPathPrefix
          env.GQL_PROJECTC_ASSET_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint_assets
          env.GQL_PROJECTC_CONNECTION_TIMEOUT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_timeout
          env.GQL_PAGE_DEFAULT_ROWS = getEnvConfig.getEnvConfig(env.BRANCH_NAME).page_default_rows
          env.GQL_PAGE_MAX_ROWS = getEnvConfig.getEnvConfig(env.BRANCH_NAME).page_max_rows
          env.GQL_ACCOUNT_SERVICE_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).account_service_endpoint
          env.GQL_ASSET_SERVICE_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).asset_service_endpoint
          env.GQL_PREFERENCE_SERVICE_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).preference_service_endpoint
          env.GQL_PROJECTC_USERS_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint_users
          env.GQL_PROJECTC_HEALTH_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint_health
          env.GQL_PROJECTC_SETTINGS_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint_settings
          env.GQL_PROJECTC_LANGUAGES_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint_languages
          env.GQL_PROJECTC_ENDPOINT = getEnvConfig.getEnvConfig(env.BRANCH_NAME).projectc_endpoint
          env.DAAS_ENDPOINTURL = getEnvConfig.getEnvConfig(env.BRANCH_NAME).daas_endpointurl
          env.DAAS_BASIC_AUTH_USERNAME = getEnvConfig.getEnvConfig(env.BRANCH_NAME).daas_basic_auth_username
          env.DAAS_BASIC_AUTH_PASSWORD = getEnvConfig.getEnvConfig(env.BRANCH_NAME).daas_basic_auth_password
                  
          dir("${WORKSPACE}/environment/${PROJECT_NAME}/${ENV_CONFIG_DIR}/") {
            fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}")])
          }
          sh 'printenv'
        }
