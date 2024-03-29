#!/usr/bin/env bash

set -eu

echo 'export ENVIRONMENT=demo'

# urls
echo "export SERVICE_AUTH_PROVIDER_API_BASE_URL=http://rpe-service-auth-provider-demo.service.core-compute-demo.internal"
echo "export IDAM_API_BASE_URL=https://idam-api.demo.platform.hmcts.net"
echo "export CCD_IDAM_REDIRECT_URL=https://www-ccd.demo.platform.hmcts.net/oauth2redirect"
echo "export CCD_DEFINITION_STORE_API_BASE_URL=http://ccd-definition-store-api-demo.service.core-compute-demo.internal"

# definition placeholders
echo "export CCD_DEF_CASE_SERVICE_BASE_URL=http://probate-back-office-demo.service.core-compute-demo.internal"
