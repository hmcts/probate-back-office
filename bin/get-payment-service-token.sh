#!/bin/bash
binFolder=$(dirname "$0")

serviceToken=$(${binFolder}/idam-lease-service-token.sh payment_app $(docker run --rm toolbelt/oathtool --totp -b AAAAAAAAAAAAAAAB))
echo $serviceToken
