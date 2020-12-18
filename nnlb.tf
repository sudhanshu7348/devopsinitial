resource "aws_lb" "swarm-nlb" {
  count = var.env == "prod" ? 1 : 0
  name = "${var.team_name}-${var.app_name}-${var.env}-nlb"

  internal           = true
  load_balancer_type = "network"
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

resource "aws_lb_listener" "swarm-nlb-listener" {
  count = var.env == "prod" ? 1 : 0
  load_balancer_arn = "${aws_lb.swarm-nlb[count.index].arn}"

  port              = 22
  protocol          = "TCP"
  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.swrm-nlb[count.index].arn
  }
}

resource "aws_lb_target_group" "swarm-nlb" {
  count = var.env == "prod" ? 1 : 0
  name     = "${var.team_name}-${var.app_name}-${var.env}-nlb-tg"
  port     = 22
  protocol = "TCP"
  vpc_id   = "${var.vpc_id}"
  
  tags = "${merge(
    var.common_tags,
    map(
      "Name", "${var.team_name}_${var.app_name}_${var.env}_nlb-tg"
    )
  )}"
}
