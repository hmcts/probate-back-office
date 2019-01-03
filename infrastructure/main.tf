provider "vault" {
  //  # It is strongly recommended to configure this provider through the
  //  # environment variables described above, so that each user can have
  //  # separate credentials set in the environment.
  //  #
  //  # This will default to using $VAULT_ADDR
  //  # But can be set explicitly
  address = "https://vault.reform.hmcts.net:6200"
}


// data "vault_generic_secret" "idam_backend_service_key" {
//   path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/probate-backend"
// }

// data "vault_generic_secret" "govNotifyApiKey" {
//   path = "secret/${var.vault_section}/probate/probate_bo_govNotifyApiKey"
// }

# data "vault_generic_secret" "pdf_service_grantSignatureBase64" {
#   path = "secret/${var.vault_section}/probate/pdf_service_grantSignatureBase64"
# }

provider "azurerm" {
  version = "1.19.0"
}

locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
    
  //probate_frontend_hostname = "probate-frontend-aat.service.core-compute-aat.internal"
  previewVaultName = "${var.raw_product}-aat"
  previewEnv= "aat"
  nonPreviewEnv = "${var.env}"
  nonPreviewVaultName = "${var.raw_product}-${var.env}"
  vaultName = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
  localenv = "${(var.env == "preview" || var.env == "spreview") ? local.previewEnv : local.nonPreviewEnv}"
  pdf_service_grantSignatureBase64 = "${data.azurerm_key_vault_secret.pdf_service_grantSignatureBase64_first.value}${data.azurerm_key_vault_secret.pdf_service_grantSignatureBase64_last.value}"
}

data "azurerm_key_vault" "probate_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "govNotifyApiKey" {
  name = "probate-bo-govNotifyApiKey"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "pdf_service_grantSignatureBase64_first" {
  name = "pdf-service-grantSignatureBase64-first"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "pdf_service_grantSignatureBase64_last" {
  name = "pdf-service-grantSignatureBase64-last"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "microservicekey-probate-backend"
  vault_uri = "https://s2s-${local.localenv}.vault.azure.net/"
}

module "probate-back-office" {
  source = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  is_frontend  = false
  subscription = "${var.subscription}"
  asp_name     = "${var.asp_name}"
  capacity     = "${var.capacity}"
  common_tags  = "${var.common_tags}"
  asp_rg       = "${var.asp_rg}"
  appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"

  app_settings = {

	  // Logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"  

    DEPLOYMENT_ENV= "${var.deployment_env}"

    AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.azurerm_key_vault_secret.s2s_key.value}"
    //AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.idam_backend_service_key.data["value"]}"
    //PDF_SERVICE_GRANTSIGNATUREBASE64 = "${data.vault_generic_secret.pdf_service_grantSignatureBase64.data["value"]}"
    PDF_SERVICE_GRANTSIGNATUREBASE64 = "${local.pdf_service_grantSignatureBase64}"

    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${var.idam_service_api}"
    PDF_SERVICE_URL = "${var.pdf_service_api_url}"
    PRINTSERVICE_HOST = "${var.printservice_host}"
    PRINTSERVICE_INTERNAL_HOST = "${var.printservice_internal_host}"
    CCD_GATEWAY_HOST = "${var.ccd_gateway_host}"
    IDAM_SERVICE_HOST = "${var.idam_service_api}"
    FEE_API_URL = "${var.fee_api_url}"
    EVIDENCE_MANAGEMENT_HOST = "${var.evidence_management_host}"
    NOTIFICATIONS_GOVNOTIFYAPIKEY = "${data.azurerm_key_vault_secret.govNotifyApiKey.value}"
    java_app_name = "${var.microservice}"
    LOG_LEVEL = "${var.log_level}"
    //ROOT_APPENDER = "JSON_CONSOLE" //Remove json logging
  }
}

