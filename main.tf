 provider "aws" {
  version = "~> 2.25"
  region  = var.region
}

#######################################
# Provisioning steps:
# ----------------------
# 1. Create key pair [NOTE: Currently just using a single key pair for both (change this)]
#   1.1 Manager key
#   1.2 Worker key
# 2. Create IAM Roles
#   2.1 Manager Role
#   2.2 Worker Role
# 3. Create instance profile
# 4. Create security groups
# 5. Create the EFS for persistent data storage on manager nodes
# 6. Create the ALB + Target group
# 7. Create the manager asg
#   7.1 Ensure the nodes are being placed inside the discovery pool
# 8. Create the worker asg
# 9. Provision swarm via discovery bucket
#######################################

resource "aws_key_pair" "deployer" {
  key_name   = "${var.team_name}_${var.app_name}_${var.env}"
  public_key = file("${path.module}/${var.pub_key_path}")
}

resource "aws_iam_instance_profile" "swarm-profile" {
  name = "${var.team_name}_${var.app_name}_${var.env}"
  role = module.swarm-role.role_name
}

module "swarm-role" {
  source            = "./modules/iam"
  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name
}


module "swarm-security-groups" {
  source = "./modules/security-groups"
  vpc_id = var.vpc_id

  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name
}

module "swarm-efs" {
  source          = "./modules/efs"
  kms_key_id      = var.efskey_arn
  subnet_ids      = var.subnet_ids
  security_groups = [module.swarm-security-groups.swarm-sg-id]

  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name
}

module "manager-asg" {
  source = "./modules/autoscaling"
  vpc_id = var.vpc_id

  desired_size = var.manager_size

  efs_id            = module.swarm-efs.efs-id
  discovery_bucket  = var.discovery_bucket
  swarm_role        = "manager"
  instance_profile  = aws_iam_instance_profile.swarm-profile.id
  instance_count    = var.instance_count
  ami               = var.ami
  instance_type     = var.manager_instance_type
  subnet_ids        = var.subnet_ids
  security_group    = module.swarm-security-groups.swarm-sg-id
  key_name          = aws_key_pair.deployer.key_name
  private_key       = file("${path.module}/ssh/key.pem")
  kms_key_arn       = var.efskey_arn
  target_group_arns = module.swarm-alb.tg_arn
  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name

  root_volume_size  = 100
  # depends_on = [
  #   module.swarm-efs,
  #   aws_iam_instance_profile.swarm-profile,
  #   module.swarm-security-groups,
  #   aws_key_pair.deployer
  #   ]
}


module "worker-asg" {
  source = "./modules/autoscaling"
  vpc_id = var.vpc_id

  desired_size = var.worker_size

  efs_id           = module.swarm-efs.efs-id
  discovery_bucket  = var.discovery_bucket
  swarm_role       = "worker"
  instance_profile = aws_iam_instance_profile.swarm-profile.id
  instance_count   = var.instance_count
  ami              = var.ami
  instance_type    = var.worker_instance_type
  subnet_ids       = var.subnet_ids
  security_group   = module.swarm-security-groups.swarm-sg-id
  key_name         = aws_key_pair.deployer.key_name
  private_key      = file("${path.module}/ssh/key.pem")
  kms_key_arn      = var.efskey_arn

  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name

  root_volume_size  = 100
  # depends_on = [
  #   module.swarm-efs,
  #   aws_iam_instance_profile.swarm-profile,
  #   module.swarm-security-groups,
  #   aws_key_pair.deployer,
  #   module.manager-asg
  #   ]
}

module "swarm-alb" {
  source = "./modules/loadbalancer"
  vpc_id = var.vpc_id

  asg_name        = module.manager-asg.autoscaling-group-name
  log_bucket      = var.discovery_bucket
  iam_role        = aws_iam_instance_profile.swarm-profile.role
  subnet_ids      = var.subnet_ids
  security_groups = ["${module.swarm-security-groups.elb-sg-id}"]

  common_tags       = local.common_tags
  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name

  # depends_on = [
  #   module.manager-asg
  # ]
}

module "ssm-parameter-store" {
  source = "./modules/ssm"

  efs_id            = module.swarm-efs.efs-id
  efs_dir           = "/mnt/efs"
  discovery_bucket  = var.discovery_bucket

  env               = var.env
  organization_name = var.organization_name
  team_name         = var.team_name
  app_name          = var.app_name
}
