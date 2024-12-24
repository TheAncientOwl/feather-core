#!/bin/bash

source $FEATHER_CORE_ROOT/project/scripts/env.sh

function feather_coverage_help() {
    print "${DARK_GRAY}» ${DARK_AQUA}--coverage${DARK_GRAY}/${DARK_AQUA}-k: ${RESET}run unit tests coverage"

}

function feather_coverage() {
    feather_print "${DARK_AQUA}Running unit tests coverage"
    rm -rf ~/feathercore-tmp 2>/dev/null
    mvn clean jacoco:prepare-agent install jacoco:report
    cp $FEATHER_CORE_ROOT/project/coverage/resources/* $FEATHER_CORE_ROOT/target/site/jacoco/jacoco-resources/.
    rm -rf ~/feathercore-tmp 2>/dev/null
}
