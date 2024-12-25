#!/bin/bash

# » env variables
source $FEATHER_CORE_ROOT/project/scripts/env.sh

source $FEATHER_CORE_ROOT/project/scripts/modules/clean.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/install.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/run.sh

function feather_dev_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--dev${DARK_GRAY}       | ${DARK_AQUA}-d: ${RESET}clean install + run server"
}

function feather_dev() {
    feather_clean
    feather_install
    feather_run
}
