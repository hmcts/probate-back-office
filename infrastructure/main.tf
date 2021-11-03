provider "azurerm" {
  features {}
}

locals {
  vaultName = "${var.product}-${var.env}"
  app_full_name = "${var.product}-${var.component}"
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

}
