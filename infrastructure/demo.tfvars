env = "demo"
deployment_env = "preprod"

vault_section = "preprod"

packages_environment = "preprod"
packages_version = "3.0.0"


outbound_proxy = ""
log_level = "INFO"

idam_service_api = "http://rpe-service-auth-provider-demo.service.core-compute-demo.internal"
pdf_service_api_url = "http://cmc-pdf-service-demo.service.core-compute-demo.internal"
printservice_internal_host = "http://ccd-case-print-service-demo.service.core-compute-demo.internal"
printservice_host = "https://return-case-doc.ccd.demo.platform.hmcts.net"
fee_api_url = "https://preprod.fees-register.reform.hmcts.net:4411"
evidence_management_host = "http://dm-store-demo.service.core-compute-demo.internal"
idam_user_host = "http://betaPreProdccidamAppLB.reform.hmcts.net:4502" //not used need to remove