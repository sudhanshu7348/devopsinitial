data "aws_vpc" "main" {
  id = var.vpc_id
}

data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}

locals {
  common_tags = {
    "Organization Name" = var.organization_name
    "Team Name"         = var.team_name
    "App Team Name"     = var.app_team_name
    "App Name"          = var.app_name
    "Env"               = var.env
    "Region"            = var.region
    "Provisioner"       = "terraform"
  }
}

variable "vpc_id" {
  type = string
}

variable "region" {
  description = "Region to deploy swarm to"
  type        = string
  default     = "us-east-1"
}
variable "discovery_bucket" {
  description = "S3 bucket to place swarm join tokens/manager IPs/ALB access logs"
  type    = string
}

variable "pub_key_path" {
  description = "Local path of the deployer pub key"
  type        = string
}
variable "prv_key_path" {
  description = "Local path of the deployer ssh key"
  type        = string
}

# variable "ebskey_arn" {
#   description = "ARN for the KMS key to encrypt EBS snapshots with"
#   type        = string
# }

variable "efskey_arn" {
  description = "ARN for the KMS key to encrypt EFS with"
  type        = string
}
variable "s3key_arn" {
  description = "ARN for the S3 KMS key to encrypt EBS snapshots with"
  type        = string
}

# #############################################################################
# EC2 + Launch Config
# #############################################################################
variable "subnet_ids" {
  type = list(string)
}

variable "instance_count" {
  description = "Number of EC2 instances to create from module deployment"
  type        = number
  default     = 0
}
variable "manager_size" {
  description = "Desired number of EC2 manager nodes (MUST BE AN ODD NUMBER and probably won't need more than 3)"
  type        = number
  default     = 3
}
variable "worker_size" {
  description = "Desired number of EC2 worker nodes"
  type        = number
  default     = 3
}

variable "manager_instance_type" {
  description = "Type of instance to use with the AMI"
  type        = string
  default     = "t3.medium"
}

variable "worker_instance_type" {
  description = "Type of instance to use with the AMI"
  type        = string
  default     = "t3.xlarge"
}

variable "ami" {
  description = "Optional Amazon Machine Image specification for the EC2 modules"
  type        = string
}

# #############################################################################
# Tags
# #############################################################################
variable "env" {
  description = "Environment to be configured"
  type        = string
}

variable "organization_name" {
  description = "Organization that will own resource"
  type        = string
}

variable "team_name" {
  description = "Team that will own resource"
  type        = string
}
variable "app_team_name" {
  description = "App team that will own resource"
  type        = string
}

variable "app_name" {
  description = "Name of the application the deployment is for"
  type        = string
}
