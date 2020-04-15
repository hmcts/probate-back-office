provider "azurerm" {
  version = "=1.44.0"
}

locals {
  vaultName = "${var.product}-${var.env}"
}
