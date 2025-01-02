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
source $FEATHER_CORE_ROOT/project/scripts/modules/dev.sh

# » variables
PLUGINS_PATH="${FEATHER_CORE_ROOT}/dev/server/plugins"

# » check for no arguments
if [ $# -eq 0 ]; then
    feather_print "${DARK_GRAY}«${YELLOW} Help ${DARK_GRAY}»"
    feather_clean_help
    feather_configure_help
    feather_coverage_help
    feather_dev_help
    feather_headers_help
    feather_install_help
    feather_run_help
    feather_test_help
    exit 1
fi

# » process arguments
while [[ $# -gt 0 ]]; do
    flag=$1

    feather_print "${DARK_AQUA}Processing '${flag}'"

    case "$flag" in
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
        if [[ $# -gt 1 && $2 != -* ]]; then
            feather_test $2
            shift
        else
            feather_test
        fi
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

exit 0
