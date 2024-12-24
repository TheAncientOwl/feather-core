#!/bin/bash

# » env variables
source $FEATHER_CORE_ROOT/project/scripts/env.sh

source $FEATHER_CORE_ROOT/project/scripts/modules/install.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/clean.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/run.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/headers.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/configure.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/test.sh
source $FEATHER_CORE_ROOT/project/scripts/modules/coverage.sh

# » variables
PLUGINS_PATH="${FEATHER_CORE_ROOT}/dev/server/plugins"

# » helpers
function print_feather_help() {
    feather_print "${DARK_GRAY}«${YELLOW} Help ${DARK_GRAY}»"
    print "${DARK_GRAY}» ${DARK_AQUA}--help${DARK_GRAY}: ${RESET}display this menu"
    print "${DARK_GRAY}» ${DARK_AQUA}--clean${DARK_GRAY}/${DARK_AQUA}-c${DARK_GRAY}: ${RESET}remove the plugin files from dev server location"
    print "${DARK_GRAY}» ${DARK_AQUA}--configure${DARK_GRAY}/${DARK_AQUA}-x${DARK_GRAY}: ${RESET}configure maven project"
    print "${DARK_GRAY}» ${DARK_AQUA}--install${DARK_GRAY}/${DARK_AQUA}-i${DARK_GRAY}: ${RESET}install the plugin at dev server location"
    print "${DARK_GRAY}» ${DARK_AQUA}--run${DARK_GRAY}/${DARK_AQUA}-r${DARK_GRAY}: ${RESET}run the dev server"
    print "${DARK_GRAY}» ${DARK_AQUA}--dev${DARK_GRAY}/${DARK_AQUA}-d: ${RESET}clean install + server run"
    print "${DARK_GRAY}» ${DARK_AQUA}--headers${DARK_GRAY}/${DARK_AQUA}-h${DARK_GRAY}: ${RESET}setup headers"
    print "${DARK_GRAY}» ${DARK_AQUA}--test${DARK_GRAY}/${DARK_AQUA}-t${DARK_GRAY}: ${RESET}run unit tests"
    print "${DARK_GRAY}» ${DARK_AQUA}--coverage${DARK_GRAY}/${DARK_AQUA}-k: ${RESET}run unit tests coverage"
}

# » help check
if [ $# -eq 0 ]; then
    feather_print "${DARK_RED}No arguments were specified"
    print_feather_help
    exit 1
fi

while [[ $# -gt 0 ]]; do
    flag=$1

    feather_print "${DARK_AQUA}Processing '${flag}'"

    case "$flag" in
    --help)
        print_feather_help
        ;;
    --clean | -c)
        feather_clean
        ;;
    --install | -i)
        feather_install
        ;;
    --run | -r)
        feather_run
        ;;
    --configure | -x)
        feather_configure
        ;;
    --test | -t)
        feather_test
        ;;
    --headers | -h)
        feather_headers
        ;;
    --coverage | -k)
        feather_coverage
        ;;
    --dev | -d)
        feather_clean
        feather_install
        feather_run
        ;;
    *)
        feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}${flag}${DARK_RED}'"
        ;;
    esac

    shift

    if [[ $# -gt 0 ]]; then
        echo ""
    fi
done
