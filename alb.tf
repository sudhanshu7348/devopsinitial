resource "aws_lb" "swarm-alb" {
  name = "${var.team_name}-${var.app_name}-${var.env}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = tolist(var.security_groups)
  subnets            = tolist(var.subnet_ids)

  # access_logs {
  #   bucket     = var.log_bucket
  #   enabled    = true
  # }

  tags = "${merge(
    var.common_tags,
    map(
      "Name", "${var.team_name}_${var.app_name}_${var.env}"
    )
  )}"
}

resource "aws_lb_listener" "swarm-alb-listener" {
  load_balancer_arn = "${aws_lb.swam-alb.arn}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.swarm.arn
  }
}
