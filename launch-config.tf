resource "aws_launch_configuration" "swarm" {
  name_prefix = "${var.team_name}-docker-swarm-lc"

  associate_public_ip_address = false
  iam_instance_profile        = var.instance_profile
  image_id                    = var.ami

  security_groups = [var.security_group]
  instance_type   = var.instance_type
  key_name        = var.key_name

  root_block_device {
    encrypted   = true
    iops        = 100
    volume_size = var.root_volume_size
    volume_type = "gp2"
  }

  lifecycle {
    create_before_destroy = true
  }

  user_data = data.template_file.user-data.rendered
}
