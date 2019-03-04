#!/bin/bash
############################
# Descrypt Images
# dave.jones@hmcts.net
# 
# Note to produce the hex values you can use the following URL:
# https://codebeautify.org/string-hex-converter
#
# Usage: ./bin/bo-decrypt-image.sh [image] [image-out] [128bit key]
# Example ./bin/bo-decrypt-image.sh signature.png.enc signature.png password11111111
#
# The IV in this implementation is static as the encryped file will not change,
# so the same IV here needs to be used in the code to decrypt.
#

IMAGE_FILENANE_IN=$1
IMAGE_FILENANE_OUT=$2
KEY_STR=${3:-'dummykey'}
IV_STR="P3oba73En3yp7ion"

KEY=$(xxd -p -l 16 <<< "$KEY_STR")
IV=$(xxd -p -l 16 <<< "$IV_STR")

openssl enc -aes-128-cbc -d -base64 -A -in "$IMAGE_FILENANE_IN" -out "$IMAGE_FILENANE_OUT" -K $KEY -iv $IV
