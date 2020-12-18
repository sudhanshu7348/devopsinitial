variable "vpc_id" {
  type = string
}

variable "log_bucket" {
  type    = string
}

variable "region" {
  description = "Region to deploy swarm to"
  type        = string
  default     = "us-east-1"
}

variable "iam_role" {
  description = "Role to be assumed by the swarm manager and workers"
  type        = string
}

variable "security_groups" {
  description = "Security group to assign to ec2 instance"
  type        = list(string)
}
variable "subnet_ids" {
  description = "Subnet IDs to instantiate load balancer within"
  type        = list(string)
}

variable "asg_name" {
  description = "Name of the autoscaling group to tie the load balancer to"
  type        = string
}


# #############################################################################
# Tags
# #############################################################################
variable "common_tags" {
  description = "Common tags"
  type        = map(string)
}

variable "env" {
  description = "Environment to be configured"
  type        = string
}

variable "organization_name" {
  description = "Environment type to be configured"
  type        = string
}

variable "team_name" {
  description = "Environment type to be configured"
  type        = string
}

variable "app_name" {
  description = "Name of the application the deployment is for"
  type        = string
}
