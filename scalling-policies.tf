

resource "aws_autoscaling_policy" "swarm-scaleup-policy" {
  name                   = "swarm-scaleUpPolicy"
  scaling_adjustment     = 2
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 300
  autoscaling_group_name = aws_autoscaling_group.swarm.name
}

# resource "aws_autoscaling_policy" "swarm-scaledown-policy" {
#   name                   = "swarm-scaleDownPolicy"
#   scaling_adjustment     = -1
#   adjustment_type        = "ChangeInCapacity"
#   cooldown               = 300
#   autoscaling_group_name = aws_autoscaling_group.swarm.name
# }
