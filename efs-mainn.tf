resource "aws_efs_file_system" "swarm" {
  creation_token = "${var.team_name}-${var.app_name}-${var.env}"
  encrypted      = true
  kms_key_id     = var.kms_key_id

  lifecycle_policy {
    transition_to_ia = "AFTER_14_DAYS"
  }

  tags = merge(
    var.common_tags,
    {
      "Name" = "${var.team_name}_${var.app_name}_${var.env}"
    },
  )
}

resource "aws_efs_mount_target" "subnet-targets" {
  count = length(var.subnet_ids)
  file_system_id = aws_efs_file_system.swarm.id
  subnet_id = element(var.subnet_ids, count.index)
  security_groups = var.security_groups
}
