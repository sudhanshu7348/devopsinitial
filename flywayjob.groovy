def call(){

    def projectName = ""
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

    dir("${WORKSPACE}/environment/${PROJECT_NAME}/resources/") {
        fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*', targetLocation: "${WORKSPACE}/src/main/resources/")])
    }

    dir("${WORKSPACE}/environment") {
        deleteDir();
    }

    sh 'echo ${namespacePassed}'
    sh 'printenv'
    if(env.BRANCH.equals("develop")){
    sh '/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/maven/bin/mvn clean -Dflyway.outOfOrder=true flyway:migrate -P ${namespacePassed} '
    }
    else{
     sh '/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/maven/bin/mvn clean flyway:migrate -P ${namespacePassed}'
         }

}
            
