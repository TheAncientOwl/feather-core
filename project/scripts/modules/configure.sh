#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_configure_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--configure${DARK_GRAY} | ${DARK_AQUA}-x${DARK_GRAY}: ${RESET}configure maven project"
}

function feather_configure() {
    feather_print "${DARK_AQUA}Configuring project based on pom.xml"
    mvn eclipse:clean -f "pom.xml"
    mvn eclipse:eclipse -f "pom.xml"
    feather_print "${DARK_AQUA}Configuration done"
}
