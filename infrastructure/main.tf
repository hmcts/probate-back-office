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



locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
}

module "probate-sol-ccd-service" {
  source = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  is_frontend  = false
  subscription = "${var.subscription}"
  asp_name     = "${var.product}-${var.env}-asp"

  app_settings = {

	  // Logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"  

    DEPLOYMENT_ENV= "${var.deployment_env}"

    AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.idam_backend_service_key.data["value"]}"

    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${var.idam_service_api}"
    PDF_SERVICE_URL = "${var.pdf_service_api_url}"
    PRINTSERVICE_HOST = "${var.printservice_host}"
    IDAM_USER_HOST = "${var.idam_user_host}"
    IDAM_SERVICE_HOST = "${var.idam_service_api}"
    FEE_API_URL = "${var.fee_api_url}"
    EVIDENCE_MANAGEMENT_HOST = "${var.evidence_management_host}" 

    java_app_name = "${var.microservice}"
    LOG_LEVEL = "${var.log_level}"
    ROOT_APPENDER = "JSON_CONSOLE"

  }
}

module "probate-sol-ccd-service-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  name                = "pro-sol-ccd-ser-${var.env}"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.probate-sol-ccd-service.resource_group_name}"
  product_group_object_id = "68839600-92da-4862-bb24-1259814d1384"
}