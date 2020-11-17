provider "azurerm" {
  features {}
}

locals {
  vaultName = "${var.product}-${var.env}"
}
