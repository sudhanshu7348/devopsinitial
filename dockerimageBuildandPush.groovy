def call(){
	
if(IS_DEPLOYABLE.equals("true")){
	if(env.DEPLOYMENT_REGION && env.DEPLOYMENT_REGION.equals("EU")){
	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${CREDENTIAL_ID_EU}",usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']])
	{
	sh '''
          echo "In EU Block"
          docker build -t ${PROJECT_NAME}-${NAMESPACE} --build-arg dockertarget=${dockertarget} .	 
	  eval "( envsubst < k8s-config-template.yaml ) > k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml"	
	  docker login --username=$USERNAME --password=$PASSWORD  ${EU_ARTIFACTORY_URL}
          docker tag ${PROJECT_NAME}-${NAMESPACE}:latest ${EU_ARTIFACTORY_URL}/${ARTIFACTORY_NAME}/${NAMESPACE}/${PROJECT_NAME}-${NAMESPACE}:${PROJECT_VERSION}
          docker push ${EU_ARTIFACTORY_URL}/${ARTIFACTORY_NAME}/${NAMESPACE}/${PROJECT_NAME}-${NAMESPACE}:${PROJECT_VERSION}
        '''
	}
	}
																	
	
	else
	{
		withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${CREDENTIAL_ID}",usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']])
		{
		sh '''
		  docker build -t ${PROJECT_NAME}-${NAMESPACE} --build-arg dockertarget=${dockertarget} .
	       	  eval "( envsubst < k8s-config-template.yaml ) > k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml"
		  curl -H 'X-JFrog-Art-Api: AKCp5cbwSvMxtT2h7WvyRXVtGTdN6FLQwREYBxvVhkEMqShVEK4D9K5SNJAanjpYbJtMY7yGh' -T ${WORKSPACE}/k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml "https://${ARTIFACTORY_URL}/artifactory/generic-cdx-all/K8s-Deploy-All/US/${NAMESPACE}/k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml"
	  	  docker login --username=$USERNAME --password=$PASSWORD  ${ARTIFACTORY_URL}
        	  docker tag ${PROJECT_NAME}-${NAMESPACE}:latest ${ARTIFACTORY_URL}/${ARTIFACTORY_NAME}/${NAMESPACE}/${PROJECT_NAME}-${NAMESPACE}:${PROJECT_VERSION}
         	  docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_NAME}/${NAMESPACE}/${PROJECT_NAME}-${NAMESPACE}:${PROJECT_VERSION}	
	
		'''
		}
	}
	
	}
}

		  /*
	  curl -H 'X-JFrog-Art-Api: AKCp5cbwSvMxtT2h7WvyRXVtGTdN6FLQwREYBxvVhkEMqShVEK4D9K5SNJAanjpYbJtMY7yGh' -T ${WORKSPACE}/k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml "https://${EU_ARTIFACTORY_URL}/artifactory/generic-cdx-all/K8s-Deploy-All/EU/${NAMESPACE}/k8s-${NAMESPACE}-${PROJECT_NAME}-config.yaml"
          
	  */	
