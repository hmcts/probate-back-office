#!/usr/bin/env bash

case "$OSTYPE" in
  linux*)   CCD_DEF_CASE_SERVICE_BASE_URL="${CCD_DEF_CASE_SERVICE_BASE_URL:-$(/sbin/ip -o -4 addr list tun0 | awk '{print $4}' | cut -d/ -f1):4104}" ;; #https://unix.stackexchange.com/a/8521
  darwin*)  CCD_DEF_CASE_SERVICE_BASE_URL="${CCD_DEF_CASE_SERVICE_BASE_URL:-docker.for.mac.localhost:4104}" ;;  #https://stackoverflow.com/a/33828925 for other OS'es
esac
