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


  common_tags = var.common_tags
  name        = "${var.database_name_v14}-postgres-db-v14"
  pgsql_databases = [
    {
      name : var.database_name_v14
    }
  ]

  pgsql_version = "14"

  admin_user_object_id = var.jenkins_AAD_objectId
}


