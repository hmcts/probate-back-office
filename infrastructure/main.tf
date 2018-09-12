provider "vault" {
  //  # It is strongly recommended to configure this provider through the
  //  # environment variables described above, so that each user can have
  //  # separate credentials set in the environment.
  //  #
  //  # This will default to using $VAULT_ADDR
  //  # But can be set explicitly
  address = "https://vault.reform.hmcts.net:6200"
}


data "vault_generic_secret" "idam_backend_service_key" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/probate-backend"
}

data "vault_generic_secret" "govNotifyApiKey" {
  path = "secret/${var.vault_section}/probate/probate_bo_govNotifyApiKey"
}

data "vault_generic_secret" "pdf_service_grantSignatureBase64" {
  path = "secret/${var.vault_section}/probate/pdf_service_grantSignatureBase64"
}


locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
    
  previewVaultName    = "pro-bo"
  nonPreviewVaultName = "pro-bo-${var.env}"
  vaultName           = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
  nonPreviewVaultUri  = "${module.probate-back-office-vault.key_vault_uri}"
  previewVaultUri     = "https://pro-bo-aat.vault.azure.net/"
  vaultUri            = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultUri : local.nonPreviewVaultUri}"
}

module "probate-back-office" {
  source = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  is_frontend  = false
  subscription = "${var.subscription}"
  asp_name     = "${var.product}-${var.env}-asp"
  capacity     = "${var.capacity}"
  common_tags  = "${var.common_tags}"
  
  app_settings = {

	  // Logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"  

    DEPLOYMENT_ENV= "${var.deployment_env}"

    AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.idam_backend_service_key.data["value"]}"
    PDF_SERVICE_GRANTSIGNATUREBASE64 = "${data.vault_generic_secret.pdf_service_grantSignatureBase64.data["value"]}"

    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${var.idam_service_api}"
    PDF_SERVICE_URL = "${var.pdf_service_api_url}"
    PRINTSERVICE_HOST = "${var.printservice_host}"
    PRINTSERVICE_INTERNAL_HOST = "${var.printservice_internal_host}"
    IDAM_SERVICE_HOST = "${var.idam_service_api}"
    FEE_API_URL = "${var.fee_api_url}"
    EVIDENCE_MANAGEMENT_HOST = "${var.evidence_management_host}"
    NOTIFICATIONS_GOVNOTIFYAPIKEY = "${data.vault_generic_secret.govNotifyApiKey.data["value"]}"
    java_app_name = "${var.microservice}"
    LOG_LEVEL = "${var.log_level}"
    //ROOT_APPENDER = "JSON_CONSOLE" //Remove json logging
    DEMO_TEST = "Testing Demo for update config"
  }
}

module "probate-back-office-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  name                = "${local.vaultName}"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.probate-back-office.resource_group_name}"
  product_group_object_id = "33ed3c5a-bd38-4083-84e3-2ba17841e31e"
}
