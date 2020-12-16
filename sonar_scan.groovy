#!groovy
def call(Map pipelineParams) {
    sh  """
        #!/bin/bash
        docker build \
            --target scanner \
            --build-arg SONAR_URL="${env.sonarUrl}" \
            --build-arg PROJECT_KEY=${pipelineParams.artifactName} \
            --build-arg VERSION_NO=${pipelineParams.projectVersion} \
            .
        """
}
