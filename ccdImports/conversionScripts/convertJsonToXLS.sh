#!/usr/bin/env bash

set -eu

excel_output_directory="jsonToXLS"
mkdir -p ${excel_output_directory}

source ${0%/*}/toAbsPath.sh
source ${0%/*}/setHostAndPort.sh

# TODO pull latest docker image
# see https://stackoverflow.com/questions/26734402/how-to-upgrade-docker-container-after-its-image-changed

ccd_definition_json_output_dir_absolute_path=$(to-abs-path "$1")
ccd_definition_json_output_dir_name=$(echo ${ccd_definition_json_output_dir_absolute_path##*/})
ccd_definition_excel_output_file=$(to-abs-path "${excel_output_directory}/${ccd_definition_json_output_dir_name}.xlsx")

echo $ccd_definition_excel_output_file

if [[ ! -e ${ccd_definition_excel_output_file} ]]; then
   touch ${ccd_definition_excel_output_file}
fi

docker run --rm --name json2xlsx \
    -v ${ccd_definition_json_output_dir_absolute_path}:/tmp/ccd-definition \
    -v ${ccd_definition_excel_output_file}:/tmp/ccd-definition.xlsx \
    -e CCD_DEF_CASE_SERVICE_BASE_URL \
    hmcts/ccd-definition-processor:latest \
    json2xlsx -D /tmp/ccd-definition -o /tmp/ccd-definition.xlsx
