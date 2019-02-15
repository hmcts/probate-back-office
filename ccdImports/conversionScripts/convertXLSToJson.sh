#!/usr/bin/env bash

set -eu

json_relative_path="xlsToJson"

source ${0%/*}/toAbsPath.sh
source ${0%/*}/setHostAndPort.sh

echo ${CCD_DEF_CASE_SERVICE_BASE_URL}

ccd_definition=$1

#extract filename https://stackoverflow.com/a/2664746
ccd_definition_file=$(echo ${ccd_definition##*/})
ccd_definition_file_without_extension=$(echo ${ccd_definition_file%.xlsx})

ccd_definition_absolute_path=$(to-abs-path "${ccd_definition}")

mkdir -p ${json_relative_path}/${ccd_definition_file_without_extension}
ccd_definition_json_output_dir_absolute_path=$(to-abs-path "${json_relative_path}/${ccd_definition_file_without_extension}")

echo "Output json :" $ccd_definition_json_output_dir_absolute_path
mkdir -p $ccd_definition_json_output_dir_absolute_path

# TODO pull latest docker image
# see https://stackoverflow.com/questions/26734402/how-to-upgrade-docker-container-after-its-image-changed

docker run --rm --name xlsx2json \
  -v ${ccd_definition_absolute_path}:/tmp/ccd-definition \
  -v ${ccd_definition_json_output_dir_absolute_path}:/tmp/ccd-definition-json \
  -e CCD_DEF_CASE_SERVICE_BASE_URL \
  docker.artifactory.reform.hmcts.net/ccd/ccd-definition-processor:latest \
  xlsx2json -D /tmp/ccd-definition-json -i /tmp/ccd-definition
