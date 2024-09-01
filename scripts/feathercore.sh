#!/bin/bash

# » console colors
DARK_RED="\033[31m"
DARK_GREEN="\033[32m"
YELLOW="\033[93m"
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
  print "${TAG}${DARK_GRAY}«${YELLOW} Help ${DARK_GRAY}»"
  print "${DARK_GRAY}» ${DARK_AQUA}--help${DARK_GRAY}/${DARK_AQUA}-h${DARK_GRAY}: ${RESET}display this menu"
  print "${DARK_GRAY}» ${DARK_AQUA}--configure${DARK_GRAY}/${DARK_AQUA}-x${DARK_GRAY}: ${RESET}configure maven project"
  print "${DARK_GRAY}» ${DARK_AQUA}--clean${DARK_GRAY}/${DARK_AQUA}-c${DARK_GRAY}: ${RESET}remove the plugin files from dev server location"
  print "${DARK_GRAY}» ${DARK_AQUA}--install${DARK_GRAY}/${DARK_AQUA}-i${DARK_GRAY}: ${RESET}install the plugin at dev server location"
  print "${DARK_GRAY}» ${DARK_AQUA}--run${DARK_GRAY}/${DARK_AQUA}-r${DARK_GRAY}: ${RESET}run the dev server"
}

if [ $# -eq 0 ]; then
  feather_print "${DARK_RED}No arguments were specified"
  print_help
  exit 1
fi

# » flags
install=false
clean=false
run=false
configure=false

while [[ $# -gt 0 ]]; do
  flag=$1

  if [[ "$flag" != "-"* ]]; then
    feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}$flag${DARK_RED}'"
    shift
    continue
  fi

  if [[ "$flag" == "--"* ]]; then
    if [[ "$flag" == "--help" ]]; then
      print_help
    elif [[ "$flag" == "--clean" ]]; then
      feather_print "${DARK_AQUA}Detected 'clean' flag"
      clean=true
    elif [[ "$flag" == "--install" ]]; then
      feather_print "${DARK_AQUA}Detected 'install' flag"
      install=true
    elif [[ "$flag" == "--run" ]]; then
      feather_print "${DARK_AQUA}Detected 'run server' flag"
      run=true
    elif [[ "$flag" == "--configure" ]]; then
      feather_print "${DARK_AQUA}Detected 'configure' flag"
      configure=true
    else
      feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}$flag${DARK_RED}'"
    fi

    shift
    continue
  fi

  length=${#flag}
  for (( i=1; i<length; i++ )); do
    case "${flag:$i:1}" in
      h)
      print_help
      ;;
      c)
      feather_print "${DARK_AQUA}Detected 'clean' flag"
      clean=true
      ;;
      i)
      feather_print "${DARK_AQUA}Detected 'install' flag"
      install=true
      ;;
      r)
      feather_print "${DARK_AQUA}Detected 'run server' flag"
      run=true
      ;;
      x)
      feather_print "${DARK_AQUA}Detected 'configure' flag"
      configure=true
      ;;
      *)
      feather_print "${DARK_RED}Unknown flag: '${LIGHT_RED}${flag:$i:1}${DARK_RED}'"
      ;;
    esac 
  done

  shift
done

# » execute flags
if [ "$configure" = true ]; then
  feather_print "${DARK_AQUA}Configuring project based on pom.xml"
  mvn eclipse:eclipse -f "pom.xml"
  feather_print "${DARK_AQUA}Configuration done"
fi

if [ "$clean" = true ]; then
  feather_print "${DARK_AQUA}Removing FeatherCore files from ${PLUGINS_PATH}"
  rm -rf ${PLUGINS_PATH}/FeatherCore*
  feather_print "${DARK_AQUA}FeatherCore files removed from ${PLUGINS_PATH}"
fi

if [ "$install" = true ]; then
  feather_print "${DARK_AQUA}Installing plugin to ${PLUGINS_PATH}"
  mvn clean install -X
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
