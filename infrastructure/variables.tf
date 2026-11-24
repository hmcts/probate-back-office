// Infrastructural variables
variable "product" {
  default = "probate"
}

variable "component" {}

variable "database_name" {
  default = "probatemandb"
}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}

variable "aks_subscription_id" {}

// CNP settings
variable "jenkins_AAD_objectId" {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "tenant_id" {
  description = "(Required) The Azure Active Directory tenant ID that should be used for authenticating requests to the key vault. This is usually sourced from environemnt variables and not normally required to be specified."
}

variable "common_tags" {
  type = map(string)
}

variable "subnet_suffix" {
  default     = "expanded"
  type        = string
  description = "Suffix to append to the subnet name, the originally created one used by this module is full in a number of environments."
}