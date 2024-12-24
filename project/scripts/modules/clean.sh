#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_clean_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--clean${DARK_GRAY}/${DARK_AQUA}-c${DARK_GRAY}: ${RESET}remove the plugin files from dev server location"
}

function remove_files() {
    feather_print "${DARK_AQUA}Removing FeatherCore files from ${1}"
    rm -rf ${1}/FeatherCore*
    feather_print "${DARK_AQUA}FeatherCore files removed from ${1}"
}

function feather_clean() {
    remove_files $PLUGINS_PATH
    remove_files $FEATHER_CORE_ROOT/target

    feather_print "${DARK_AQUA}Removing ${FEATHER_CORE_ROOT}/target"
    rm -rf $FEATHER_CORE_ROOT/target
    feather_print "${DARK_AQUA}${FEATHER_CORE_ROOT}/target removed"
}
