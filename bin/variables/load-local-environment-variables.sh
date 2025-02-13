#!/usr/bin/env bash

set -eu

# Clear the file:
> .aat-env

# Safe urls
echo "EVIDENCE_MANAGEMENT_HOST=http://dm-store-aat.service.core-compute-aat.internal" >> .aat-env
echo "EVIDENCE_MANAGEMENT_URL=http://dm-store-aat.service.core-compute-aat.internal" >> .aat-env
echo "CASE_DOCUMENT_AM_URL=http://ccd-case-document-am-api-aat.service.core-compute-aat.internal" >> .aat-env
echo "FEE_API_URL=http://fees-register-api-aat.service.core-compute-aat.internal" >> .aat-env
echo "S2S_AUTH_URL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal" >> .aat-env
echo "SERVICE_AUTH_PROVIDER_BASE_URL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal" >> .aat-env
echo "USER_AUTH_PROVIDER_OAUTH2_URL=https://idam-api.aat.platform.hmcts.net" >> .aat-env
echo "PDF_SERVICE_URL=http://cmc-pdf-service-aat.service.core-compute-aat.internal" >> .aat-env
echo "SEND_LETTER_SERIVCE_BASEURL=http://rpe-send-letter-service-aat.service.core-compute-aat.internal" >> .aat-env
echo "CCD_DRAFT_STORE_URL=http://draft-store-service-aat.service.core-compute-aat.internal" >> .aat-env
echo "TS_TRANSLATION_SERVICE_HOST=http://ts-translation-service-aat.service.core-compute-aat.internal" >> .aat-env
echo "PRD_API_URL=http://rd-professional-api-aat.service.core-compute-aat.internal" >> .aat-env
echo "PAYMENT_URL=http://payment-api-aat.service.core-compute-aat.internal" >> .aat-env
echo "ROLE_ASSIGNMENT_URL=http://localhost:4096" >> .aat-env
echo "XUI_MO_PORT=3009" >> .aat-env
echo "IDAM_SERVICE_HOST=https://idam-api.aat.platform.hmcts.net" >> .aat-env
echo "IDAM_CLIENT_NAME=probate" >> .aat-env
echo "IDAM_REDIRECT_URL=https://probate-frontend-aat.service.core-compute-aat.internal/oauth2/callback" >> .aat-env
echo "S2S_AUTHORISED_SERVICES=ccd_definition,ccd_data,xui_webapp,nfdiv_case_api,ccd_gw,probate_backend" >> .aat-env
echo "CCD_S2S_AUTHORISED_SERVICES_CASE_USER_ROLES=probate_backend" >> .aat-env
echo "ROLE_ASSIGNMENT_S2S_AUTHORISED_SERVICES=ccd_gw,am_role_assignment_service,am_org_role_mapping_service,xui_webapp,aac_manage_case_assignment,ccd_data,probate_backend" >> .aat-env
echo "DATA_STORE_S2S_AUTHORISED_SERVICES=ccd_gw,fpl_case_service,ccd_data,ccd_ps,probate_backend,payment-api,xui_webapp,ccd_case_document_am_api,am_role_assignment_service,aac_manage_case_assignment,xui_mo_webapp,probate_backend" >> .aat-env
echo "IDAM_API_URL=https://idam-api.aat.platform.hmcts.net" >> .aat-env
echo "S2S_API_URL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal" >> .aat-env
echo "CCD_IDAM_REDIRECT_URL=https://ccd-case-management-web-aat.service.core-compute-aat.internal/oauth2redirect" >> .aat-env

# Probate variables fetched from probate-aat vault
echo "AUTH_TOKEN_EMAIL=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name authTokenEmail)" >> .aat-env
echo "AUTH_TOKEN_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name authTokenPassword)" >> .aat-env
echo "IDAM_SECRET=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name idam-secret-probate)" >> .aat-env
echo "S2S_AUTH_TOTP_SECRET=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name s2sAuthTotpSecret)" >> .aat-env
echo "IMPORTER_USERNAME=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name definition-importer-username)" >> .aat-env
echo "IMPORTER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name definition-importer-password)" >> .aat-env
echo "CW_USER_ID=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name cwUserId)" >> .aat-env
echo "CW_USER_EMAIL=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name cwUserEmail)" >> .aat-env
echo "CW_USER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name cwUserPass)" >> .aat-env
echo "SCHEDULER_CASEWORKER_USERNAME=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name schedulerCaseWorkerUser)" >> .aat-env
echo "SCHEDULER_CASEWORKER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name schedulerCaseWorkerPass)" >> .aat-env
echo "SOL_USER_EMAIL=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name solicitorUserEmail)" >> .aat-env
echo "SOL_USER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name solicitorUserPass)" >> .aat-env
echo "SOL2_USER_EMAIL=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name solicitor2UserEmail)" >> .aat-env
echo "SOL2_USER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name solicitor2UserPass)" >> .aat-env
echo "PROBATE_NOTIFY_KEY="$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name probate-bo-govNotifyApiKey)" >> .aat-env

# xui variables fetched from rpx-aat vault:
echo "XUI_SYSTEM_USER_NAME=$(az keyvault secret show --vault-name rpx-aat -o tsv --query value --name system-user-name)" >> .aat-env
echo "XUI_SYSTEM_USER_PASSWORD=$(az keyvault secret show --vault-name rpx-aat -o tsv --query value --name system-user-password)" >> .aat-env
echo "XUI_LD_ID=$(az keyvault secret show --vault-name rpx-aat -o tsv --query value --name launch-darkly-client-id)" >> .aat-env

#ccd exports
echo "API_GATEWAY_IDAM_SECRET=$(az keyvault secret show --vault-name ccd-aat -o tsv --query value --name ccd-api-gateway-oauth2-client-secret)" >> .aat-env
echo "CCD_IDAM_REDIRECT_URL=https://ccd-case-management-web-aat.service.core-compute-aat.internal/oauth2redirect" >> .aat-env
