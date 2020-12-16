data "template_file" "user-data" {
  template = file("${path.module}/scripts/user-data.sh")

  vars = {
    ROLE                   = var.swarm_role
    SWARM_DISCOVERY_BUCKET = var.discovery_bucket
    EFS_ID                 = var.efs_id
    REGION                 = var.region
    APP_NAME               = var.app_name
    TEAM_NAME              = var.team_name
    ENV                    = var.env
  }
}
