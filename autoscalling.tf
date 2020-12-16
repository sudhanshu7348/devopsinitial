resource "aws_autoscaling_group" "swarm" {
  name                = "${var.team_name}_${var.app_name}_${var.env}_${var.swarm_role}"
  vpc_zone_identifier = tolist(var.subnet_ids)

  health_check_grace_period = 60
  health_check_type         = "EC2"
  force_delete              = true
  desired_capacity          = var.desired_size
  availability_zones   = data.aws_availability_zones.available.names
  launch_configuration = aws_launch_configuration.swarm.name
  min_size             = 1
  max_size             = 5

  # Important note: whenever using a launch configuration with an auto scaling
  # group, you must set create_before_destroy = true. However, as soon as you
  # set create_before_destroy = true in one resource, you must also set it in
  # every resource that it depends on, or you'll get an error about cyclic
  # dependencies (especially when removing resources). For more info, see:
  #
  # https://www.terraform.io/docs/providers/aws/r/launch_configuration.html
  # https://terraform.io/docs/configuration/resources.html
  lifecycle {
    create_before_destroy = true
  }

  target_group_arns = tolist([var.target_group_arns])

  tag {
    key                 = "Name"
    value               = "${var.team_name}_${var.app_name}_${var.env}_${var.swarm_role}"
    propagate_at_launch = true
  }

  tag {
    key                 = "Team"
    value               = var.team_name
    propagate_at_launch = true
  }

  tag {
    key                 = "Application"
    value               = var.app_name
    propagate_at_launch = true
  }

  tag {
    key                 = "Environment"
    value               = var.env
    propagate_at_launch = true
  }

  tag {
    key                 = "Provisioner"
    value               = "terraform"
    propagate_at_launch = true
  }

  tag {
    key                 = "SwarmRole"
    value               = var.swarm_role
    propagate_at_launch = true
  }
}


resource "aws_cloudwatch_metric_alarm" "swarm-high" {
  alarm_name          = "${var.team_name}_${var.app_name}_${var.env}_${var.swarm_role}_CPUUtilizationHIGH"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "120"
  statistic           = "Average"
  threshold           = "80"

  dimensions = {
    AutoScalingGroupName = "${aws_autoscaling_group.swarm.name}"
  }

  alarm_description = "This metric monitors ec2 high cpu utilization"
  alarm_actions     = ["${aws_autoscaling_policy.swarm-scaleup-policy.arn}"]
}

# resource "aws_cloudwatch_metric_alarm" "swarm-low" {
#   alarm_name          = "${var.team_name}_${var.app_name}_${var.env}_${var.swarm_role}_CPUUtilizationLOW"
#   comparison_operator = "LessThanThreshold"
#   evaluation_periods  = "2"
#   metric_name         = "CPUUtilization"
#   namespace           = "AWS/EC2"
#   period              = "120"
#   statistic           = "Average"
#   threshold           = "60"

#   dimensions = {
#     AutoScalingGroupName = "${aws_autoscaling_group.swarm.name}"
#   }

#   alarm_description = "This metric monitors ec2 low cpu utilization"
#   alarm_actions     = ["${aws_autoscaling_policy.swarm-scaledown-policy.arn}"]
# }
