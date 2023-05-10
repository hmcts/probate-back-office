// Infrastructural variables
variable "product" {
  default = "probate"
}

variable "component" {}

variable postgresql_user_v11 {
  default = "probateman_user"
}
variable database_name_v11 {
  default = "probatemandb"
}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}

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

variable "appinsights_location" {
  default     = "West Europe"
  description = "Location for Application Insights"
}
