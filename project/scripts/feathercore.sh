#!/bin/bash

# » env variables
source $FEATHER_CORE_ROOT/project/scripts/env.sh

# » variables
PLUGINS_PATH="${FEATHER_CORE_ROOT}/dev/server/plugins"

# » helpers
function print_feather_help() {
    feather_print "${DARK_GRAY}«${YELLOW} Help ${DARK_GRAY}»"
    print "${DARK_GRAY}» ${DARK_AQUA}--help${DARK_GRAY}: ${RESET}display this menu"
    print "${DARK_GRAY}» ${DARK_AQUA}--configure${DARK_GRAY}/${DARK_AQUA}-x${DARK_GRAY}: ${RESET}configure maven project"
    print "${DARK_GRAY}» ${DARK_AQUA}--clean${DARK_GRAY}/${DARK_AQUA}-c${DARK_GRAY}: ${RESET}remove the plugin files from dev server location"
    print "${DARK_GRAY}» ${DARK_AQUA}--install${DARK_GRAY}/${DARK_AQUA}-i${DARK_GRAY}: ${RESET}install the plugin at dev server location"
    print "${DARK_GRAY}» ${DARK_AQUA}--verbose${DARK_GRAY}/${DARK_AQUA}-v${DARK_GRAY}: ${RESET}print verbose messages for install phase"
    print "${DARK_GRAY}» ${DARK_AQUA}--run${DARK_GRAY}/${DARK_AQUA}-r${DARK_GRAY}: ${RESET}run the dev server"
    print "${DARK_GRAY}» ${DARK_AQUA}--headers${DARK_GRAY}/${DARK_AQUA}-h${DARK_GRAY}: ${RESET}run unit tests"
    print "${DARK_GRAY}» ${DARK_AQUA}--test${DARK_GRAY}/${DARK_AQUA}-t${DARK_GRAY}: ${RESET}run unit tests"
    print "${DARK_GRAY}» ${DARK_AQUA}--coverage${DARK_GRAY}: ${RESET}run unit tests coverage"
    print "${DARK_GRAY}» ${DARK_AQUA}--dev${DARK_GRAY}: ${RESET}clean install + server run"
}

# » help check
if [ $# -eq 0 ]; then
    feather_print "${DARK_RED}No arguments were specified"
    print_feather_help
    exit 1
fi

# » flags
install=false
clean=false
run=false
configure=false
verbose=false
tests=false
headers=false
coverage=false

while [[ $# -gt 0 ]]; do
    flag=$1

    case "$flag" in
    --help)
        print_feather_help
        ;;
    --clean | -c)
        feather_print "${DARK_AQUA}Detected 'clean' flag"
        clean=true
        ;;
    --install | -i)
        feather_print "${DARK_AQUA}Detected 'install' flag"
        install=true
        ;;
    --run | -r)
        feather_print "${DARK_AQUA}Detected 'run server' flag"
        run=true
        ;;
    --configure | -x)
        feather_print "${DARK_AQUA}Detected 'configure' flag"
        configure=true
        ;;
    --verbose | -v)
        feather_print "${DARK_AQUA}Detected 'verbose' flag"
        verbose=true
        ;;
    --test | -t)
        feather_print "${DARK_AQUA}Detected 'test' flag"
        test=true
        ;;
    --headers | -h)
        feather_print "${DARK_AQUA}Detected 'headers' flag"
        headers=true
        ;;
    --coverage)
        feather_print "${DARK_AQUA}Detected 'coverage' flag"
        coverage=true
        ;;
    --dev)
        feather_print "${DARK_AQUA}Detected 'dev' flag"
        clean=true
        install=true
        run=true
        ;;
    *)
        feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}${flag}${DARK_RED}'"
        ;;
    esac

    shift
done

# » execute flags
if [ "$configure" = true ]; then
    feather_print "${DARK_AQUA}Configuring project based on pom.xml"
    mvn eclipse:clean -f "pom.xml"
    mvn eclipse:eclipse -f "pom.xml"
    feather_print "${DARK_AQUA}Configuration done"
fi

function remove_files() {
    feather_print "${DARK_AQUA}Removing FeatherCore files from ${1}"
    rm -rf ${1}/FeatherCore*
    feather_print "${DARK_AQUA}FeatherCore files removed from ${1}"
}

if [ "$clean" = true ]; then
    remove_files $PLUGINS_PATH
    remove_files $FEATHER_CORE_ROOT/target

    feather_print "${DARK_AQUA}Removing ${FEATHER_CORE_ROOT}/target"
    rm -rf $FEATHER_CORE_ROOT/target
    feather_print "${DARK_AQUA}${FEATHER_CORE_ROOT}/target removed"
fi

if [ "$install" = true ]; then
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
fi

if [ "$test" = true ]; then
    feather_print "${DARK_AQUA}Running unit tests"
    mvn test
fi

if [ "$coverage" = true ]; then
    feather_print "${DARK_AQUA}Running unit tests coverage"
    mvn clean jacoco:prepare-agent install jacoco:report
fi

if [ "$run" = true ]; then
    feather_print "${DARK_AQUA}Starting development server"
    $FEATHER_CORE_ROOT/project/scripts/mongodb.sh -xs
    cd $FEATHER_CORE_ROOT/dev/server
    ./start.sh
    cd $FEATHER_CORE_ROOT
    feather_print "${DARK_AQUA}Development server stopped"
    $FEATHER_CORE_ROOT/project/scripts/mongodb.sh -x
fi

if [ "$headers" = true ]; then
    feather_print "${DARK_AQUA}Setting up files header"
    $FEATHER_CORE_ROOT/project/scripts/setup_files_header.sh*
fi
