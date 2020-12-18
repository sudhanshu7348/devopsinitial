resource "aws_lb_target_group" "swarm" {
  name     = "${var.team_name}-${var.app_name}-${var.env}"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc_id}"
  
  tags = "${merge(
    var.common_tags,
    map(
      "Name", "${var.team_name}_${var.app_name}_${var.env}"
    )
  )}"
}

# resource "aws_autoscaling_attachment" "swarm_asg_attachment" {
#   autoscaling_group_name = var.asg_name
#   alb_target_group_arn   = aws_lb_target_group.swarm.arn
# }
