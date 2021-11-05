provider "azurerm" {
  features {}
}

locals {
  vaultName = "${var.product}-${var.env}"
}

module "db-v11" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=postgresql_tf"
  product            = var.product
  component          = var.component
  name               = "probatemandb-postgres-db-v11"
  location           = var.location
  env                = var.env
  database_name      = "${var.component}-${var.env}"
  postgresql_user    = var.product
  postgresql_version = 11
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = var.common_tags
  subscription       = var.subscription
}
