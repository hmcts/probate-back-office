output "vaultUri" {
  value = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}
