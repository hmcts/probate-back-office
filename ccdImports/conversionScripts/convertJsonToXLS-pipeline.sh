#!/usr/bin/env bash

set -eu

root_dir=$(realpath $(dirname ${0})/../..)
echo root_dir = $root_dir
excel_output_directory=${root_dir}/build/"jsonToXLS"-"$2"
mkdir -p ${excel_output_directory}

source ${0%/*}/toAbsPath.sh
source ${0%/*}/setHostAndPort.sh

# TODO pull latest docker image
# see https://stackoverflow.com/questions/26734402/how-to-upgrade-docker-container-after-its-image-changed

ccd_definition_json_output_dir_absolute_path=$(to-abs-path "$1")
ccd_definition_json_output_dir_name=$(echo ${ccd_definition_json_output_dir_absolute_path##*/})
ccd_definition_excel_output_file=$(to-abs-path "${excel_output_directory}/${ccd_definition_json_output_dir_name}.xlsx")
additionalParameters=${3-}

definition_input_dir=${ccd_definition_json_output_dir_absolute_path}

echo Output Directory excel_output_directory = $excel_output_directory

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
echo CCD_DEF_AAC_URL=${CCD_DEF_AAC_URL}

docker run --rm --name json2xlsx \
    -v ${definition_tmp_dir}:/tmp/ccd-definition \
    -v ${definition_tmp_out_file}:/tmp/ccd-definition.xlsx \
    -e CCD_DEF_CASE_SERVICE_BASE_URL=${CCD_DEF_CASE_SERVICE_BASE_URL} \
    -e CCD_DEF_AAC_URL=${CCD_DEF_AAC_URL} \
    hmctspublic.azurecr.io/ccd/definition-processor:latest \
    json2xlsx -D /tmp/ccd-definition -o /tmp/ccd-definition.xlsx ${additionalParameters}

cp "$definition_tmp_out_file"  "$excel_output_directory"
