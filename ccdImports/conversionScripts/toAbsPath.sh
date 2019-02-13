#!/usr/bin/env bash
#https://stackoverflow.com/a/51264222
function to-abs-path {
    local target="$1"

    if [ "$target" == "." ]; then
        echo "$(pwd)"
    elif [ "$target" == ".." ]; then
        echo "$(dirname "$(pwd)")"
    else
        echo "$(cd "$(dirname "$1")"; pwd)/$(basename "$1")"
    fi
}