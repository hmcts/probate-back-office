#!/usr/bin/env bash

set -eu

find $1 -type f -exec sed -i "" "s/$2/\${CCD_DEF_CASE_SERVICE_BASE_URL}/g" {} \;
