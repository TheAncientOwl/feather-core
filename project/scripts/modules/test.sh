#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_test() {
    feather_print "${DARK_AQUA}Running unit tests"
    rm -rf ~/feathercore-tmp 2>/dev/null
    mvn test
    rm -rf ~/feathercore-tmp 2>/dev/null
}
