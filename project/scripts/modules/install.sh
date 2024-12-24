#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_install_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--install${DARK_GRAY}/${DARK_AQUA}-i${DARK_GRAY}: ${RESET}install the plugin at dev server location"
}

function feather_install() {
    feather_print "${DARK_AQUA}Installing plugin to ${PLUGINS_PATH}"
    if [ "$verbose" = true ]; then
        feather_print "${DARK_AQUA}Verbose: ON"
        mvn clean package shade:shade -X
    else
        feather_print "${DARK_AQUA}Verbose: OFF"
        mvn clean package shade:shade
    fi
    cp target/FeatherCore* ${PLUGINS_PATH}
    feather_print "${DARK_AQUA}Plugin installed to ${PLUGINS_PATH}"

    rm -rf ~/feathercore-tmp 2>/dev/null
}
