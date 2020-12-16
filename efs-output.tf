output "efs-id" {
  value       = aws_efs_file_system.swarm.id
  description = "ID of the EFS (e.g fs-ccfc0d65)"
}

output "efs-arn" {
  value       = aws_efs_file_system.swarm.arn
  description = "ARN of the EFS"
}

output "efs-dns" {
  value       = aws_efs_file_system.swarm.dns_name
  description = "DNS name of the EFS"
}
