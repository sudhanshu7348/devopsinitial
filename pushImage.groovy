#!groovy
def call(Map pipelineParams) {
    buildImage(pipelineParams)
    sh  """
        #!/bin/bash
        docker --config ~/.${pipelineParams.dockerRepo} login --username ${artifactoryCreds_USR} --password ${artifactoryCreds_PSW} ${env.dockerRegistry}
        docker --config ~/.${pipelineParams.dockerRepo} push ${env.dockerRegistry}/${pipelineParams.dockerRepo}/${pipelineParams.artifactName}:${pipelineParams.appBranch}-${pipelineParams.projectVersion}
        """
}
