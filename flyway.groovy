   def call(){
   
       if(BRANCH_NAME.equals("develop") || BRANCH_NAME.equals("release") || BRANCH_NAME.equals("master")){
    //  env.NAMESPACE_PASSED= "${namespacePassed}"
            build job: 'MobileFlywayMigrationPipeline', parameters: [string(name: 'namespacePassed', value: env.NAMESPACE), string(name: 'branch', value: env.BRANCH_NAME)]
       }
      else{
              build job: 'MobileFlywayMigrationPipeline', parameters: [string(name: 'namespacePassed', value: env.NAMESPACE), string(name: 'branch', value: "develop")]

         }
            }
            
