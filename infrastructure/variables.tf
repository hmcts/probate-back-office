// Infrastructural variables

variable "product" {
  default = "probate"
}

variable "microservice" {
  default = "back-office"
}

variable "location" {
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "ilbIp" { }

variable "deployment_env" {
  type = "string"
}

variable "component" {
  default = "backend"
}

variable "subscription" {}


variable "vault_section" {
  type = "string"
}

// CNP settings
variable "jenkins_AAD_objectId" {
  type                        = "string"
  description                 = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "tenant_id" {
  description = "(Required) The Azure Active Directory tenant ID that should be used for authenticating requests to the key vault. This is usually sourced from environemnt variables and not normally required to be specified."
}

variable "outbound_proxy" {
  default = "http://proxyout.reform.hmcts.net:8080/"
}

variable "log_level" {
  default = "INFO"
}

variable "capacity" {
  default = "1"
}

