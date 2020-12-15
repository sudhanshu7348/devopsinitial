
echo "***********************Jenkins Job Running on App2 NODE*******************"

zip -r CDX-Payments_sap_prod.zip ${WORKSPACE}

ssh hybris@10.127.168.105 "mkdir -p ~/jenkinsws"
ssh hybris@10.127.168.105 "touch ~/jenkinsws/a.txt"
ssh hybris@10.127.168.105 "rm -r ~/jenkinsws/*"


scp -v -r ${WORKSPACE}/*.zip hybris@10.127.168.105:~/jenkinsws
ssh hybris@10.127.168.105 "unzip ~/jenkinsws/*.zip -d ~/jenkinsws"


ssh hybris@10.127.168.105 "~/./CDX-Payments-SAP-PROD-Deployment.sh"

echo "****************************************************************************"

################################################ SCRIPT FROM SERVER PATH #################################################
##########################################################################################################################\

export MAVEN_HOME="/opt/apache-maven-3.6.3"
export PATH=$PATH:$MAVEN_HOME/bin
JAR_DIR="/NFS_DATA/artifacts/cybsjars"
HYBRIS_BIN="/opt/hybris/bin"
HYBRIS_CUSTOM="${HYBRIS_BIN}/custom"
HYBRIS_CONFIG_ADMIN="/opt/hybris/config/admin"
HYBRIS_CONFIG="/opt/hybris/config/"
HYBRIS_PLATFORM="/opt/hybris/bin/platform"
HYBRIS_CUSTOMIZE="/opt/hybris/config/admin/customize"
TOMCAT_HOME="/opt/tomcat/bin"
ANT_HOME="/opt/hybris/bin/platform/apache-ant/bin"
BUILD_ENV="prod"
WORKSPACE_SAP="/home/hybris/jenkinsws/var/lib/jenkins/workspace/CDX-Payments-SAP-PROD-Job"
NODE="app2"

echo "****************************BUILD**********************************************"

echo "**************************Platform Preperation*******************************"
if [ -d "$HYBRIS_CUSTOM" ]; then
    echo "[INFO] Remove old custom extensions."
    rm -rf ${HYBRIS_CUSTOM}
    
fi

echo "[INFO] Copy custom extension to HYBRIS platform."
cp -rf ${WORKSPACE_SAP}/custom ${HYBRIS_BIN}
echo "[INFO] Copy necessary configs to HYBRIS platform."
cp -rf ${WORKSPACE_SAP}/config/${BUILD_ENV}/admin/* ${HYBRIS_CONFIG}


echo "******************************************************************************"

echo "**************************ADDON INSTALLATION*******************************"

mvn install:install-file -Dfile=${JAR_DIR}/cybs-api-2.2.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-api -Dversion=2.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/cybs-ws-client-1.148.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-ws-client -Dversion=1.148.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/cybs-report-client-1.1.0.jar -DgroupId=com.cybersource.reporting -DartifactId=cybs-report-client -Dversion=1.1.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/spock-retry-0.6.1.jar -DgroupId=com.anotherchrisberry -DartifactId=spock-retry -Dversion=0.6.1 -Dpackaging=jar


echo "**************************BUILD & DEPLOYMENT***************************************"


echo ${ANT_HOME}
(cd ${TOMCAT_HOME} && pwd && ./catalina.sh stop)
(cd ${HYBRIS_BIN} && mv custom custom_$(date +"%m-%d-%Y")_$(date +"%T"))
mkdir -p /NFS_DATA/artifacts/custom-backup
(cd ${HYBRIS_BIN} && mv custom_$(date +"%m-%d-%Y")_$(date +"%T") /NFS_DATA/artifacts/custom-backup/custom_$(date +"%m-%d-%Y")_$(date +"%T"))
rm -rf custom_$(date +"%m-%d-%Y")_$(date +"%T")
cp -r ${WORKSPACE_SAP}/custom ${HYBRIS_BIN}
cp -r ${WORKSPACE_SAP}/config/customize/*  ${HYBRIS_CUSTOMIZE}
cp -r ${WORKSPACE_SAP}/config/${BUILD_ENV}/admin/customize/* ${HYBRIS_CUSTOMIZE}
(cd ${HYBRIS_PLATFORM} && . ./setantenv.sh)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant customize)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant clean all)
(cd ${TOMCAT_HOME} && pwd && ./catalina.sh start)
