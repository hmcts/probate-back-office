provider "azurerm" {
  features {}
}

output "vaultName" {
  value = local.vaultName
}
