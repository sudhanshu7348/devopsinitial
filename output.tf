output "alb_dns_name" {
  value = aws_lb.swarm-alb.dns_name
}

output "alb_arn" {
    value = aws_lb.swarm-alb.arn
}

output "tg_name" {
    value = aws_lb_target_group.swarm.name
}

output "tg_arn" {
    value = aws_lb_target_group.swarm.arn
}
