#!/usr/bin/env bash

branchName=$1

#Checkout specific branch camunda bpmn definition
echo "Pull wa-standalone-task-bpmn"
git clone https://github.com/hmcts/wa-standalone-task-bpmn.git
cd wa-standalone-task-bpmn

echo "Switch to ${branchName} branch on wa-standalone-task-bpmn"
git checkout ${branchName}
cd ..

#Copy camunda folder which contains bpmn files
cp -r ./wa-standalone-task-bpmn/src/main/resources/ .
rm -rf ./wa-standalone-task-bpmn
