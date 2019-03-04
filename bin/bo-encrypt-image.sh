#!/bin/bash
############################
# Encrypt Images
# dave.jones@hmcts.net
# 
# Note to produce the hex values you can use the following URL:
# https://codebeautify.org/string-hex-converter
#
# Usage: ./bin/bo-encrypt-image.sh [image] [128bit key]
# Example ./bin/bo-encrypt-image.sh signature.png password11111111
#
# The key and iv need to be 16 bytes long (16 characters alpha numeric). 
#
# The IV in this implementation is static as the encryped file will not change,
# so the same IV here needs to be used in the code to decrypt.
#

IMAGE_FILENANE=$1
KEY_STR=${2:-'dummykey'}
IV_STR="P3oba73En3yp7ion"

KEY=$(xxd -p -l 16 <<< "$KEY_STR")
IV=$(xxd -p -l 16 <<< "$IV_STR")

openssl enc -aes-128-cbc -base64 -A -in "$IMAGE_FILENANE" -out "$IMAGE_FILENANE".enc -K $KEY -iv $IV
