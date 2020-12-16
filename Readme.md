# Terraform + AWS + Docker Swarm setup
###  Expected Output: A fully scalable docker swarm upon AWS

Use this deployment to create the following:
* EC2 key pair
* EC2 instance profile
* Security groups for EC2 instances and ALB
* ~~S3 bucket for swarm discovery/logging with necessary bucket policies and private configurations~~
* Application Load Balancer to tie network traffic to the autoscaling group
* Target Group to forward traffic on port 80
* Manager and worker autoscaling groups
* Provisioned swarm via discovery bucket

This deployment **DOES NOT** provision any services onto the Swarm (such as Portainer or Jenkins)

## Installation 
**Terraform v0.12** - How to install terraform you can find [here](https://www.terraform.io/intro/getting-started/install.html)

## Preparations
#### S3 Swarm Discovery Bucket
Due to the global, high availability nature of S3 buckets, creation and deletion of the buckets within the same deployment causes instability when building/destroying. Therefore, one must be prepared beforehand and specified in the `terraform.tfvars`.

#### SSH keys
Before starting, create ssh keys. Terraform will create key-pair in AWS, based on these keys. See [how to create ssh keys](https://confluence.atlassian.com/bitbucketserver/creating-ssh-keys-776639788.html)
Create a pem file with private ssh key you generated. Terraform will need to the pem file to connect to instances for provisioning.
Place the generated pem file in a local path and specify it in the `terraform.tfvars`.

#### Configuration Arguments
See `templates.tfvars` for the minimum variables required for deployment and create your configuration inside a `terraform.tfvars` file.
See `variables.tf` for descriptions and type declarations for said variables.

## How to use
After all configuration files are ready, you can initialize the modules:
```
terraform init
```

now you can check your configuration is valid with your AWS instance
```
make plan
```
This command will show either syntax errors or list of resources will be created. After you can run:
```
make apply
```
This command will build and run all resources in the *.tf files. If you run this command many times, Terraform will destroy previous instances before creating new ones. 
That is it. Now you have fully functioned docker swarm cluster in AWS.

If you want to terminate instances and destroy the configuration you may call:
```
make destroy
```

## Working with User Data Script
The user-data script is responsible for provisioning each new instance spun up by the autoscaling group. This resource can be found in `./modules/autoscaling/scripts`

### Troubleshooting User Data
> Note: The script is compiled into a single execution line, and causes the entire script to fail from a single error message.


Output logs from execution for each node can be found in `/var/log/cloud-init-output.log` and `/var/log/cloud-init.log`

Changes to user-data for an already deployed swarm will only effect newly spun up instances, and the already existing EC2 nodes will be untouched.

For instances where the swarm managers have not joined the cluster, run the following command to run auto-discovery code:

```bash
// Script that runs using the information provided in the .terraform-output-cache
make swarm-managers-provision
```

For packages that failed to install or if the EFS failed to mount, SSH into the lacking instance(s) and  `sudo su` and run the commands present in `./modules/autoscaling/scripts/user-data.sh`

## Future plans
- Move the provisioning scripts into GitHub / S3 / SSM
- Execute provisioning scripts via SSM on a lifecycle hook
- Move the auto discovery bucket into a GH repository
- Store NFS file structure for each swarm deployment within GitHub
- Move all package installations/commands that require root permissions into an AMI
