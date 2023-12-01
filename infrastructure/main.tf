provider "azurerm" {
  features {}
}

provider "azurerm" {
  features {}
  skip_provider_registration = true
  alias                      = "cft_vnet"
  subscription_id            = var.aks_subscription_id
}

locals {
  vaultName = "${var.product}-${var.env}"
}

module "db-v11" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=postgresql_tf"
  product            = var.product
  component          = var.component
  name               = "${var.database_name_v11}-postgres-db-v11"
  location           = var.location
  env                = var.env
  database_name      = var.database_name_v11
  postgresql_user    = var.postgresql_user_v11
  postgresql_version = 11
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = var.common_tags
  storage_mb         = 61440
  subscription       = var.subscription
}

module "postgresql" {
  providers = {
    azurerm.postgres_network = azurerm.cft_vnet
  }

  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  env    = var.env

  product       = var.product
  component     = var.component
  business_area = "cft"

  force_user_permissions_trigger = "0"

  common_tags = var.common_tags
  name        = "${var.database_name}-postgres-flexible-db"

  subnet_suffix = "expanded"

  pgsql_databases = [
    {
      name : var.database_name
    }
  ]

  pgsql_version = "15"

  admin_user_object_id = var.jenkins_AAD_objectId
}

data "azurerm_key_vault" "probate_key_vault" {
  name                = local.vaultName
  resource_group_name = local.vaultName
}

resource "azurerm_key_vault_secret" "POSTGRES-USER-FLEX" {
  name         = "${var.database_name}-POSTGRES-USER-FLEX"
  value        = module.postgresql.username
  key_vault_id = data.azurerm_key_vault.probate_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS-FLEX" {
  name         = "${var.database_name}-POSTGRES-PASS-FLEX"
  value        = module.postgresql.password
  key_vault_id = data.azurerm_key_vault.probate_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST-FLEX" {
  name         = "${var.database_name}-POSTGRES-HOST-FLEX"
  value        = module.postgresql.fqdn
  key_vault_id = data.azurerm_key_vault.probate_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT-FLEX" {
  name         = "${var.database_name}-POSTGRES-PORT-FLEX"
  value        = "5432"
  key_vault_id = data.azurerm_key_vault.probate_key_vault.id
}
