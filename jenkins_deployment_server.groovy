def getDeploymentServer(branch) {
  if(env.DEPLOYMENT_REGION && env.DEPLOYMENT_REGION.equals("EU")){
    if (branch.toLowerCase().equals("master")) {
      return env.ssh_kube_eu_prod_master
    }/* else if (branch.toLowerCase().equals("jenkins-test")){
      env.NAMESPACE = 'prod';
      env.APP_ACTIVE_PROFILE = 'dev';
      env.ENV_CONFIG_DIR = 'dev';
      return env.ssh_kube_eu_prod_master
    }*/
    return env.ssh_kube_eu_non_prod_master
  }
  else {
    if (branch.toLowerCase().equals("master")) {
      return env.ssh_kube_prod_master
    }
    return env.ssh_kube_master
  }
}
