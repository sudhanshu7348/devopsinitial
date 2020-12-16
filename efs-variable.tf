variable "kms_key_id" {
  description = "ID for the KMS key to encrypt EFS volume with"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnets to create targets for EFS"
  type        = list(string)
}

variable "security_groups" {
  description = "List of sgs to create targets for EFS"
  type        = list(string)
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
