echo "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"

export MAVEN_HOME="/home/hybris/apache-maven-3.6.3"
export PATH=$PATH:$MAVEN_HOME/bin
JAR_DIR="/home/hybris/.m2/jars/Cybersource_JARS"
HYBRIS_BIN="/apps/hybris/hybris/bin"
HYBRIS_CUSTOM="${HYBRIS_BIN}/custom"
HYBRIS_CONFIG="/apps/hybris/hybris/config"
HYBRIS_PLATFORM="${HYBRIS_BIN}/platform"
HYBRIS_CUSTOMIZE="/apps/hybris/hybris/config/customize"
#TOMCAT_HOME="/opt/tomcat/bin"
ANT_HOME="${HYBRIS_BIN}/platform/apache-ant/bin"
BUILD_ENV="aws_dev"
#WORKSPACE_SAP_DEV="/home/hybris/jenkinsws"

sed -i 's/symmetric.key.file.2=/symmetric.key.file.2=Generated-KEYSIZE-Bit-AES-Key.hybris/g' ${WORKSPACE}/config/aws_dev/local.properties


echo "****************************BUILD**********************************************"
echo "**************************Platform Preperation*******************************"
if [ -d "$HYBRIS_CUSTOM" ]; then
    echo "[INFO] Remove old custom extensions."
    rm -rf ${HYBRIS_CUSTOM}
    
fi
echo "[INFO] Copy custom extension to HYBRIS platform."
cp -rf ${WORKSPACE}/custom ${HYBRIS_BIN}
echo "[INFO] Copy necessary configs to HYBRIS platform."
cp -rf ${WORKSPACE}/config/${BUILD_ENV}/* ${HYBRIS_CONFIG}
echo "******************************************************************************"
echo "**************************ADDON INSTALLATION*******************************"

#mvn install:install-file -Dfile=${JAR_DIR}/cybs-api-2.2.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-api -Dversion=2.2.0 -Dpackaging=jar
#mvn install:install-file -Dfile=${JAR_DIR}/cybs-ws-client-1.148.0.jar -DgroupId=com.cybersource.payment -DartifactId=cybs-ws-client -Dversion=1.148.0 -Dpackaging=jar
#mvn install:install-file -Dfile=${JAR_DIR}/cybs-report-client-1.1.0.jar -DgroupId=com.cybersource.reporting -DartifactId=cybs-report-client -Dversion=1.1.0 -Dpackaging=jar
#mvn install:install-file -Dfile=${JAR_DIR}/spock-retry-0.6.1.jar -DgroupId=com.anotherchrisberry -DartifactId=spock-retry -Dversion=0.6.1 -Dpackaging=jar

echo "**************************BUILD & DEPLOYMENT***************************************"
echo ${ANT_HOME}

(cd ${HYBRIS_PLATFORM} && pwd && ./hybrisserver.sh stop)
#wget https://hc-us-east-aws-artifactory.cloud.health.ge.com/artifactory/generic-cdx-all/cc_engine_sap_dev/cc_engine_custom_sap_dev.zip
#unzip ${WORKSPACE_SAP_DEV}/cc_engine_custom_sap_dev.zip
#/apps/hybris/hybris/bin/platform/hybrisserver.sh stop
#(cd ${TOMCAT_HOME} && pwd && ./catalina.sh stop)
#mv /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom_${BUILD_TIME}_${BUILD_NUMBER}
(cd ${HYBRIS_BIN} && mv custo* custom_${BUILD_NUMBER})
(cd ${HYBRIS_BIN} && mv custom_${BUILD_NUMBER} /home/hybris/custom_${BUILD_NUMBER})
#mv /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/custom_${BUILD_TIME}_${BUILD_NUMBER} /home/gecloud
cp -r ${WORKSPACE}/custom ${HYBRIS_BIN}
cp -r ${WORKSPACE}/config/${BUILD_ENV}/localextensions.xml ${HYBRIS_CONFIG}
cp -r ${WORKSPACE}/config/${BUILD_ENV}/local.properties ${HYBRIS_CONFIG}

cp -r ${WORKSPACE}/config/customize/*  ${HYBRIS_CUSTOMIZE}
cp -r ${WORKSPACE}/config/${BUILD_ENV}/customize/* ${HYBRIS_CUSTOMIZE}

#cp ${WORKSPACE_SAP_DEV}/custom/local.properties /data/HCWORKSPACE_SAP_DEV/payments/CDX-Payments/config/aws_dev/
#cp -r ${WORKSPACE}/config/customize  ${HYBRIS_CUSTOMIZE}
#cp -r ${WORKSPACE}/config/sap_dev/customize ${HYBRIS_CUSTOMIZE}
(cd ${HYBRIS_PLATFORM} && . ./setantenv.sh)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant customize)
(cd ${HYBRIS_PLATFORM} && pwd && ${ANT_HOME}/ant clean all)


(cd ${HYBRIS_PLATFORM} && pwd && ./hybrisserver.sh start)
#tail -1000f /apps/hybris/hybris/log/tomcat/console-`date +%Y%m%d`.log &
