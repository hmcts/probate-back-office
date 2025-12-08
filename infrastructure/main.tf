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

provider "azurerm" {
  alias = "delete_rg"
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

module "postgresql" {
  providers = {
    azurerm.postgres_network = azurerm.cft_vnet
  }

  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=DTSPO-27543-add-test-high-high-availability-environments"
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
