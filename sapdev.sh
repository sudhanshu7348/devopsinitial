printenv
sed -i 's/symmetric.key.file.2=/symmetric.key.file.2=Generated-KEYSIZE-Bit-AES-Key.hybris/g' ${WORKSPACE}/config/sap_dev/local.properties
cat ${WORKSPACE}/config/sap_dev/local.properties
zip -r CDX-Payments_sap_dev.zip ${WORKSPACE}
#echo "***************************Pushing Zip in Artifactory*********************"
#curl -H ${artifactory_user}:${artifactory_pass} -T ${WORKSPACE}/CDX-Payments_sap_dev.zip "https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx-all/cc_engine_${Environment}/CDX-Payments_sap_dev.zip"

ssh hybris@10.127.160.248 "touch temp.txt ~/jenkinsws"
ssh hybris@10.127.160.248 "rm -r ~/jenkinsws/*"
#ssh hybris@10.127.160.248 "wget https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx-all/cc_engine_${Environment}/CDX-Payments_sap_dev.zip -P ~/jenkinsws "
#rsync -r -v --progress -e ssh hybris@10.127.160.248:~/jenkinsws/CDX-Payments_sap_dev.zip ${WORKSPACE}/*.zip

scp -v -r ${WORKSPACE}/*.zip hybris@10.127.160.248:~/jenkinsws
ssh hybris@10.127.160.248 "unzip ~/jenkinsws/*.zip -d ~/jenkinsws"


ssh hybris@10.127.160.248 "~/./CDX-Payments-SAP-DEV-Deployment.sh" #This script runs within server, the reference is given below

#!/bin/bash -ex

ssh hybris@10.127.160.248 "set +e"

ssh hybris@10.127.160.248 "sh -c 'tail -n +0 -f /opt/hybris/log/tomcat/console.log | { sed "/INFO: Server startup in/ q" && kill $$ ;} 2>/dev/null' || exit 0"


#tail -n +0 -f /apps/hybris/hybris/log/tomcat/console-`date +%Y%m%d`.log | { sed "/INFO: Server startup in/ q" && kill $$ ;} 2>/dev/null || true
#(cd ${TOMCAT_HOME} && pwd && ./catalina.sh start)
#cat /apps/hybris/hybris/log/tomcat/console.log > log.txt

#############################################################################################################################
############################################## CDX-Payments-SAP-DEV-Deployment.sh ###########################################
#############################################################################################################################

export MAVEN_HOME="/opt/apache-maven-3.6.1"
export PATH=$PATH:$MAVEN_HOME/bin
JAR_DIR="/opt/cybsjars"
HYBRIS_BIN="/opt/hybris/bin"
HYBRIS_CUSTOM="${HYBRIS_BIN}/custom"
HYBRIS_CONFIG="/opt/hybris/config"
HYBRIS_PLATFORM="/opt/hybris/bin/platform"
HYBRIS_CUSTOMIZE="/opt/hybris/config/customize"
TOMCAT_HOME="/opt/tomcat/bin"
ANT_HOME="/opt/hybris/bin/platform/apache-ant/bin"
BUILD_ENV="sap_dev"
WORKSPACE_SAP_DEV="/home/hybris/jenkinsws/var/lib/jenkins/workspace/CDX-Payments-SAP-DEV-Job"

echo "****************************BUILD**********************************************"

echo "**************************Platform Preperation*******************************"
if [ -d "$HYBRIS_CUSTOM" ]; then
    echo "[INFO] Remove old custom extensions."
    rm -rf ${HYBRIS_CUSTOM}
    
fi

echo "[INFO] Copy custom extension to HYBRIS platform."
cp -rf ${WORKSPACE_SAP_DEV}/custom ${HYBRIS_BIN}
echo "[INFO] Copy necessary configs to HYBRIS platform."
cp -rf ${WORKSPACE_SAP_DEV}/config/${BUILD_ENV}/* ${HYBRIS_CONFIG}


echo "******************************************************************************"

echo "**************************ADDON INSTALLATION*******************************"

mvn install:install-file -Dfile=${JAR_DIR}/cybs-api-2.2.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-api -Dversion=2.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/cybs-ws-client-1.148.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-ws-client -Dversion=1.148.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/cybs-report-client-1.1.0.jar -DgroupId=com.cybersource.reporting -DartifactId=cybs-report-client -Dversion=1.1.0 -Dpackaging=jar
mvn install:install-file -Dfile=${JAR_DIR}/spock-retry-0.6.1.jar -DgroupId=com.anotherchrisberry -DartifactId=spock-retry -Dversion=0.6.1 -Dpackaging=jar


echo "**************************BUILD & DEPLOYMENT***************************************"


echo ${ANT_HOME}
#wget https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx-all/cc_engine_sap_dev/cc_engine_custom_sap_dev.zip
#unzip ${WORKSPACE_SAP_DEV}/cc_engine_custom_sap_dev.zip
#/apps/hybris/hybris/bin/platform/hybrisserver.sh stop
(cd ${TOMCAT_HOME} && pwd && ./catalina.sh stop)
#mv /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom_${BUILD_TIME}_${BUILD_NUMBER}
(cd ${HYBRIS_BIN} && mv custom custom_$(date +"%m-%d-%Y")_$(date +"%T"))
(cd ${HYBRIS_BIN} && mv custom_$(date +"%m-%d-%Y")_$(date +"%T") /NFS_DATA/custom_backup/custom_$(date +"%m-%d-%Y")_$(date +"%T"))
rm -rf custom_$(date +"%m-%d-%Y")_$(date +"%T")
#mv /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom_${BUILD_TIME}_${BUILD_NUMBER} /home/gecloud
cp -r ${WORKSPACE_SAP_DEV}/custom ${HYBRIS_BIN}
cp -r ${WORKSPACE_SAP_DEV}/config/sap_dev/localextensions.xml ${HYBRIS_CONFIG}
cp -r ${WORKSPACE_SAP_DEV}/config/sap_dev/local.properties ${HYBRIS_CONFIG}

#cp ${WORKSPACE_SAP_DEV}/custom/local.properties /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/config/aws_dev/
cp -r ${WORKSPACE_SAP_DEV}/config/customize/*  ${HYBRIS_CUSTOMIZE}
cp -r ${WORKSPACE_SAP_DEV}/config/sap_dev/customize/* ${HYBRIS_CUSTOMIZE}
(cd ${HYBRIS_PLATFORM} && . ./setantenv.sh)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant customize)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant clean all)
#tail -1000f /apps/hybris/hybris/log/tomcat/console-`date +%Y%m%d`.log
(cd ${TOMCAT_HOME} && pwd && ./catalina.sh start)
#cat /opt/hybris/log/tomcat/console.log | grep "INFO: Server startup in" | tail -n 5
#tail -f /opt/hybris/log/tomcat/console.log | grep -q "INFO: Server startup in"
