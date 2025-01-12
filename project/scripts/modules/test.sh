#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_test_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--test${DARK_GRAY}      | ${DARK_AQUA}-t${DARK_GRAY}: ${RESET}run unit tests"
}

function convert_path_to_package() {
    local path=$1
    echo "${path#src/test/java/}" | tr '/' '.'
}

function feather_test() {
    feather_print "${DARK_AQUA}Running unit tests"
    rm -rf ~/feathercore-tmp 2>/dev/null
    if [ -z "$1" ]; then
        mvn test
    else
        local package=$(convert_path_to_package "$1")
        mvn -Dtest="${package}.**" test
    fi
    rm -rf ~/feathercore-tmp 2>/dev/null
}
