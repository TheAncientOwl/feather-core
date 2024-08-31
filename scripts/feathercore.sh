#!/bin/bash

# » console colors
DARK_RED="\033[31m"
DARK_GREEN="\033[32m"
YELLOW="\033[33m"
DARK_GRAY="\033[90m"
LIGHT_GRAY="\033[37m"
LIGHT_GREEN="\033[92m"
DARK_AQUA="\033[36m"
LIGHT_RED="\033[91m"
RESET="\033[0m"

# » variables
TAG="${DARK_GRAY}[${YELLOW}FeatherCore${DARK_GRAY}]${RESET} "
PLUGINS_PATH="dev/server/plugins"

# » helpers
function print() {
  printf "$1\n${RESET}"
}

function feather_print() {
  print "${TAG}$1"
}

function print_help() {
  print "${TAG}${YELLOW}Help"
  print "${DARK_GRAY}» ${DARK_AQUA}-h${DARK_GRAY}/${DARK_AQUA}--help${DARK_GRAY}: ${RESET}display this menu"
  print "${DARK_GRAY}» ${DARK_AQUA}-c${DARK_GRAY}/${DARK_AQUA}--clean${DARK_GRAY}: ${RESET}removes the plugin files from dev server location"
  print "${DARK_GRAY}» ${DARK_AQUA}-i${DARK_GRAY}/${DARK_AQUA}--install${DARK_GRAY}: ${RESET}installs the plugin at dev server location"
  print "${DARK_GRAY}» ${DARK_AQUA}-r${DARK_GRAY}/${DARK_AQUA}--run${DARK_GRAY}: ${RESET}runs the dev server"
  print "${DARK_GRAY}» ${DARK_AQUA}-ci${DARK_GRAY}/${DARK_AQUA}-ic${DARK_GRAY}: ${RESET}clean install"
  print "${DARK_GRAY}» ${DARK_AQUA}-ir${DARK_GRAY}: ${RESET}install run"
  print "${DARK_GRAY}» ${DARK_AQUA}-cir${DARK_GRAY}/${DARK_AQUA}-icr: ${RESET}clean install run"
}

# » flags
install=false
clean=false
run=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --help|-h)
      print_help
      shift
      ;;
    --install|-i)
      install=true
      feather_print "${DARK_AQUA}Detected 'install' flag"
      shift
      ;;
    --clean|-c)
      feather_print "${DARK_AQUA}Detected 'clean' flag"
      clean=true
      shift
      ;;
    --run|-r)
      feather_print "${DARK_AQUA}Detected 'run server' flag"
      run=true
      shift
      ;;
    -ci|-ic)
      feather_print "${DARK_AQUA}Detected 'clean & install' flag"
      clean=true
      install=true
      shift
      ;;
    -ir)
      feather_print "${DARK_AQUA}Detected 'install & run server' flag"
      install=true
      run=true
      shift
      ;;
    -cir|-icr)
      feather_print "${DARK_AQUA}Detected 'clean, install & run server' flag"
      clean=true
      install=true
      run=true
      shift
      ;;
    *)
      feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}$1${DARK_RED}'"
      shift
      ;;
  esac
done

# » execute flags
if [ "$clean" = true ]; then
  feather_print "${DARK_AQUA}Removing FeatherCore files from ${PLUGINS_PATH}"
  rm -rf ${PLUGINS_PATH}/FeatherCore*
  feather_print "${DARK_AQUA}FeatherCore files removed from ${PLUGINS_PATH}"
fi

if [ "$install" = true ]; then
  feather_print "${DARK_AQUA}Installing plugin to ${PLUGINS_PATH}"
  mvn clean install
  cp target/FeatherCore* ${PLUGINS_PATH}
  feather_print "${DARK_AQUA}Plugin installed to ${PLUGINS_PATH}"
fi

if [ "$run" = true ]; then
  feather_print "${DARK_AQUA}Starting development server"
  current_path=$(pwd)
  cd dev/server
  ./start.sh
  cd $current_path
  feather_print "${DARK_AQUA}Development server stopped"
fi
