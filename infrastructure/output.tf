output "vaultUri" {
  value = "${module.probate-back-office-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${module.probate-back-office-vault.key_vault_name}"
}
