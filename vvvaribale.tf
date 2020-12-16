data "aws_availability_zones" "available" {
  state = "available"
}

variable "vpc_id" {
  type = string
}

variable "discovery_bucket" {
  type    = string
}

variable "region" {
  description = "Region to deploy swarm to"
  type        = string
  default     = "us-east-1"
}

variable "instance_profile" {
  description = "Role to be assumed by the swarm manager and workers"
  type        = string
}

variable "kms_key_arn" {
  description = "ARN for the KMS key to encrypt EBS snapshots with"
  type        = string
}

# #############################################################################
# EC2 + EBS
# #############################################################################
variable "subnet_ids" {
  type = list(string)
}

variable "efs_id" {
  type = string
}

variable "target_group_arns" {
  type = string
  default = ""
}

variable "instance_count" {
  description = "# of EC2 instances to create from module deployment"
  type        = number
  default     = 0
}

variable "desired_size" {
  description = "desired size for EC2 instances"
  type        = number
  default     = 1
}

variable "root_volume_size" {
  description = "desired size for EBS root volume"
  type        = number
  default     = 100
}

variable "block_volume_size" {
  description = "desired size for EBS block volume"
  type        = number
  default     = 50
}

variable "instance_type" {
  description = "Type of instance to use with the AMI"
  type        = string
  default     = "t2.micro"
}

variable "ami" {
  description = "Optional Amazon Machine Image specification for the EC2 modules"
  type        = string
}

variable "security_group" {
  description = "Security group to assign to ec2 instance"
  type        = string
}

variable "key_name" {
  description = "name of the deployer key"
  type        = string
}

variable "private_key" {
  description = "private key to create provisioner connection"
  type        = string
}

variable "provision_user" {
  description = "private key to create provisioner connection"
  type        = string
  default     = "ubuntu"
}

variable "docker_cmd" {
  description = "Docker command"
  default     = "sudo docker"
  type        = string
}

variable "availability" {
  description = "Availability of the node ('active'|'pause'|'drain')"
  default     = "active"
  type        = string
}

# #############################################################################
# Tags
# #############################################################################
variable "common_tags" {
  description = "Common tags"
  type        = map(string)
}

variable "swarm_role" {
  description = "manager/worker"
  type        = string
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
