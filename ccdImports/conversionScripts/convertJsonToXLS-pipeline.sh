#!/usr/bin/env bash

set -eu

#root_dir="$(cd "$(dirname "$0")"/..)"
#root_dir=../../$(dirname "$0")
root_dir=$(realpath $(dirname ${0})/../..)
echo root_dir = $root_dir
excel_output_directory=${root_dir}/build/"jsonToXLS"-"$2"
mkdir -p ${excel_output_directory}

#excel_output_directory="jsonToXLS"-"$2"
#mkdir -p ${excel_output_directory}

source ${0%/*}/toAbsPath.sh
source ${0%/*}/setHostAndPort.sh

# TODO pull latest docker image
# see https://stackoverflow.com/questions/26734402/how-to-upgrade-docker-container-after-its-image-changed

ccd_definition_json_output_dir_absolute_path=$(to-abs-path "$1")
ccd_definition_json_output_dir_name=$(echo ${ccd_definition_json_output_dir_absolute_path##*/})
ccd_definition_excel_output_file=$(to-abs-path "${excel_output_directory}/${ccd_definition_json_output_dir_name}.xlsx")

definition_input_dir=${ccd_definition_json_output_dir_absolute_path}

echo ccd_definition_json_output_dir_absolute_path = $ccd_definition_json_output_dir_absolute_path
echo ccd_definition_json_output_dir_name = $ccd_definition_json_output_dir_name
echo ccd_definition_excel_output_file = $ccd_definition_excel_output_file
echo excel_output_directory = $excel_output_directory


#ccd_definition_json_output_dir_absolute_path = /opt/jenkins/workspace/bate_probate-back-office_PR-1778/ccdImports/configFiles/CCD_Probate_Backoffice
#ccd_definition_json_output_dir_name = CCD_Probate_Backoffice
#ccd_definition_excel_output_file = /opt/jenkins/workspace/bate_probate-back-office_PR-1778/ccdImports/conversionScripts/build/jsonToXLS-preview/CCD_Probate_Backoffice.xlsx
#excel_output_directory = .././ccdImports/conversionScripts/build/jsonToXLS-preview
#definition_tmp_out_file = /tmp/jenkins-agent/probate.Da6Z04/ccd-definition/build/ccd-development-config/ccd-probate-dev.xlsx
#definition_tmp_dir = /tmp/jenkins-agent/probate.Da6Z04/ccd-definition
#root_dir =.././ccdImports/conversionScripts

#
#echo $ccd_definition_excel_output_file
#
#if [[ ! -e ${ccd_definition_excel_output_file} ]]; then
#   touch ${ccd_definition_excel_output_file}
#fi


[[ ! -d /tmp/jenkins-agent ]] && mkdir -p /tmp/jenkins-agent
definition_tmp=$(mktemp -d /tmp/jenkins-agent/probate.XXXXXX)
definition_tmp_dir="$definition_tmp/ccd-definition"
mkdir -p "$definition_tmp_dir"
cp -a ${definition_input_dir}/* "$definition_tmp_dir"
definition_tmp_out_dir="${definition_tmp_dir}/build/ccd-development-config"
[[ ! -d "$definition_tmp_out_dir" ]] && mkdir -p "$definition_tmp_out_dir"

definition_tmp_out_file="${definition_tmp_out_dir}/${ccd_definition_json_output_dir_name}.xlsx"
[[ ! -e "$definition_tmp_out_file" ]] && touch "$definition_tmp_out_file"

echo definition_tmp_out_file = $definition_tmp_out_file
echo definition_tmp_dir = $definition_tmp_dir

docker run --rm --name json2xlsx \
    -v ${definition_tmp_dir}:/tmp/ccd-definition \
    -v ${definition_tmp_out_file}:/tmp/ccd-definition.xlsx \
    -e CCD_DEF_CASE_SERVICE_BASE_URL=${CCD_DEF_CASE_SERVICE_BASE_URL} \
    hmctspublic.azurecr.io/ccd/definition-processor:latest \
    json2xlsx -D /tmp/ccd-definition -o /tmp/ccd-definition.xlsx

cp "$definition_tmp_out_file"  "$excel_output_directory"