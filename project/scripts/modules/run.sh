#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_run_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--run${DARK_GRAY}/${DARK_AQUA}-r${DARK_GRAY}: ${RESET}run the dev server"
}

function feather_run() {
    feather_print "${DARK_AQUA}Starting development server"
    $FEATHER_CORE_ROOT/project/scripts/mongodb.sh -xs
    cd $FEATHER_CORE_ROOT/dev/server
    ./start.sh
    cd $FEATHER_CORE_ROOT
    feather_print "${DARK_AQUA}Development server stopped"
    $FEATHER_CORE_ROOT/project/scripts/mongodb.sh -x
}
