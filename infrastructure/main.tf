provider "azurerm" {
  features {}
}

locals {
  app_full_name = "${var.product}-${var.component}"
  local_env = (var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env
  shared_vault_name = "${var.shared_product_name}-${local.local_env}"

  previewVaultName = "${local.app_full_name}-aat"
  nonPreviewVaultName = "${local.app_full_name}-${var.env}"
  vaultName = (var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName
}

resource "azurerm_resource_group" "rg" {
  name     = "${local.app_full_name}-${var.env}"
  location = var.location
  tags = var.common_tags
}

module "db" {
  source = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product = var.product
  component = var.component
  name = "${local.app_full_name}-postgres-db"
  location = var.location
  env = var.env
  postgresql_user = var.postgresql_user
  database_name = var.database_name
  sku_name = "GP_Gen5_2"
  sku_tier = "GeneralPurpose"
  storage_mb = "51200"
  common_tags  = var.common_tags
  subscription = var.subscription
}

data "azurerm_subnet" "postgres" {
  name                 = "core-infra-subnet-0-${var.env}"
  resource_group_name  = "core-infra-${var.env}"
  virtual_network_name = "core-infra-vnet-${var.env}"
}

module "db-v11" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=postgresql_tf"
  product            = var.product
  component          = var.component
  name               = join("-", [var.product,var.component,"postgres-db-v11"])
  location           = var.location
  env                = var.env
  postgresql_user    = var.postgresql_user_v11
  database_name      = var.database_name_v11
  postgresql_version = "11"
  subnet_id          = data.azurerm_subnet.postgres.id
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = var.common_tags
  subscription       = var.subscription
}

module "local_key_vault" {
  source = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product = local.app_full_name
  env = var.env
  tenant_id = var.tenant_id
  object_id = var.jenkins_AAD_objectId
  resource_group_name = "${local.app_full_name}-${var.env}"
  product_group_object_id = "5d9cd025-a293-4b97-a0e5-6f43efce02c0"
  common_tags = var.common_tags
  managed_identity_object_ids = [data.azurerm_user_assigned_identity.rpa-shared-identity.principal_id]
}

resource "azurerm_key_vault_secret" "POSTGRES-USER-V11" {
  name = "${var.component}-POSTGRES-USER-V11"
  value = module.db-v11.user_name
  key_vault_id = module.local_key_vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS-V11" {
  name = "${var.component}-POSTGRES-PASS-V11"
  value = module.db-v11.postgresql_password
  key_vault_id = module.local_key_vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-HOST-V11" {
  name = "${var.component}-POSTGRES-HOST-V11"
  value = module.db-v11.host_name
  key_vault_id = module.local_key_vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-PORT-V11" {
  name = "${var.component}-POSTGRES-PORT-V11"
  value = module.db-v11.postgresql_listen_port
  key_vault_id = module.local_key_vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-DATABASE-V11" {
  name = "${var.component}-POSTGRES-DATABASE-V11"
  value = module.db-v11.postgresql_database
  key_vault_id = module.local_key_vault.key_vault_id
}

data "azurerm_user_assigned_identity" "rpa-shared-identity" {
  name                = "rpa-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

data "azurerm_key_vault" "s2s_vault" {
  name = "s2s-${local.local_env}"
  resource_group_name = "rpe-service-auth-provider-${local.local_env}"
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "s2s-probate-backend"
  key_vault_id = data.azurerm_key_vault.s2s_vault.id
}


data "azurerm_key_vault_secret" "docmosis_access_key" {
  name      = "docmosis-access-key"
  key_vault_id = data.azurerm_key_vault.shared_key_vault.id
}

data "azurerm_key_vault" "shared_key_vault" {
  name = local.shared_vault_name
  resource_group_name = local.shared_vault_name
}

data "azurerm_key_vault" "product" {
  name = "${var.shared_product_name}-${var.env}"
  resource_group_name = "${var.shared_product_name}-${var.env}"
}

# Copy s2s key from shared to local vault
data "azurerm_key_vault" "local_key_vault" {
  name = module.local_key_vault.key_vault_name
  resource_group_name = module.local_key_vault.key_vault_name
}

resource "azurerm_key_vault_secret" "local_s2s_key" {
  name         = "s2s-probate-backend"
  value        = data.azurerm_key_vault_secret.s2s_key.value
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

# Copy docmosis keys to local
resource "azurerm_key_vault_secret" "local_docmosis_access_key" {
  name         = "docmosis-access-key"
  value        = data.azurerm_key_vault_secret.docmosis_access_key.value
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

# Load AppInsights key from rpa vault
data "azurerm_key_vault_secret" "app_insights_key" {
  name         = "AppInsightsInstrumentationKey"
  key_vault_id = data.azurerm_key_vault.product.id
}

resource "azurerm_key_vault_secret" "local_app_insights_key" {
  name         = "AppInsightsInstrumentationKey"
  value        = data.azurerm_key_vault_secret.app_insights_key.value
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name = "${var.component}-POSTGRES-USER"
  value = module.db.user_name
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name = "${var.component}-POSTGRES-PASS"
  value = module.db.postgresql_password
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name = "${var.component}-POSTGRES-HOST"
  value = module.db.host_name
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name = "${var.component}-POSTGRES-PORT"
  value = module.db.postgresql_listen_port
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name = "${var.component}-POSTGRES-DATABASE"
  value = module.db.postgresql_database
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}