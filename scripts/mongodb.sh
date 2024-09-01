#!/bin/bash

# » env variables
source $FEATHER_CORE_ROOT/scripts/env.sh

# » variables
MONGO_TAG="${DARK_GRAY}[${LIGHT_GREEN}MongoDB${DARK_GRAY}]${RESET} "

# » helpers
function mongo_print() {
    feather_print "${MONGO_TAG}$1"
}

function print_mongo_help() {
    mongo_print "${DARK_GRAY}«${YELLOW} Help ${DARK_GRAY}»"
    print "${DARK_GRAY}» ${DARK_AQUA}--help${DARK_GRAY}/${DARK_AQUA}-h${DARK_GRAY}: ${RESET}display this menu"
    print "${DARK_GRAY}» ${DARK_AQUA}--install${DARK_GRAY}/${DARK_AQUA}-i${DARK_GRAY}: ${RESET}install mongodb driver"
    print "${DARK_GRAY}» ${DARK_AQUA}--start${DARK_GRAY}/${DARK_AQUA}-s${DARK_GRAY}: ${RESET}start mongodb service"
    print "${DARK_GRAY}» ${DARK_AQUA}--check${DARK_GRAY}/${DARK_AQUA}-c${DARK_GRAY}: ${RESET}check mongodb service status"
    print "${DARK_GRAY}» ${DARK_AQUA}--stop${DARK_GRAY}/${DARK_AQUA}-x${DARK_GRAY}: ${RESET}stop mongodb service"
    print "${DARK_GRAY}» ${DARK_AQUA}--restart${DARK_GRAY}/${DARK_AQUA}-r${DARK_GRAY}: ${RESET}restart mongodb service"
    print "${DARK_GRAY}» ${DARK_AQUA}--daemon-reload${DARK_GRAY}/${DARK_AQUA}-d${DARK_GRAY}: ${RESET}reloads daemon"
}

# » help check
if [ $# -eq 0 ]; then
    mongo_print "${DARK_RED}No arguments were specified"
    print_mongo_help
    exit 1
fi

# » flags
install=false
start=false
check=0
stop=false
restart=false

check_and_install_dependency() {
    local package=$1

    if dpkg -l | grep -q "^ii  $package "; then
        mongo_print "$package is already installed."
    else
        mongo_print "$package is not installed. Installing..."
        sudo apt install -y $package
    fi
}

function install() {
    mongo_print "${DARK_AQUA}Detected 'install' flag"

    check_and_install_dependency gnupg
    check_and_install_dependency curl

    current_path=$(pwd)
    temp_dir="mongodb-temp"

    mkdir $temp_dir
    cd $temp_dir
    curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc |
        sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg \
            --dearmor
    echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
    sudo apt-get update
    check_and_install_dependency mongodb-org

    echo "mongodb-org hold" | sudo dpkg --set-selections
    echo "mongodb-org-database hold" | sudo dpkg --set-selections
    echo "mongodb-org-server hold" | sudo dpkg --set-selections
    echo "mongodb-mongosh hold" | sudo dpkg --set-selections
    echo "mongodb-org-mongos hold" | sudo dpkg --set-selections
    echo "mongodb-org-tools hold" | sudo dpkg --set-selections

    cd $current_path
    rm -rf $temp_dir

    mongo_print "${DARK_AQUA}MongoDB driver installed successfully"
}

function start() {
    mongo_print "${DARK_AQUA}Detected 'start' flag"
    sudo systemctl start mongod
    mongo_print "${DARK_AQUA}Service started"
}

function daemon_reload {
    mongo_print "${DARK_AQUA}Detected 'daemon-reload' flag"
    sudo systemctl daemon-reload
    mongo_print "${DARK_AQUA}Daemon reloaded"
}

function check_status() {
    mongo_print "${DARK_AQUA}Detected 'check status' flag"
    sudo systemctl status mongod
}

function stop() {
    mongo_print "${DARK_AQUA}Detected 'stop' flag"
    sudo systemctl stop mongod
    mongo_print "${DARK_AQUA}Service stopped"
}

function restart() {
    mongo_print "${DARK_AQUA}Detected 'restart' flag"
    sudo systemctl restart mongod
    mongo_print "${DARK_AQUA}Service restarted"
}

function unknown_flag() {
    mongo_print "${DARK_RED}Unknown flag: '${LIGHT_RED}$1${DARK_RED}'"
}

while [[ $# -gt 0 ]]; do
    flag=$1

    if [[ "$flag" != "-"* ]]; then
        unknown_flag $flag
        shift
        continue
    fi

    if [[ "$flag" == "--"* ]]; then
        if [[ "$flag" == "--help" ]]; then
            print_mongo_help
        elif [[ "$flag" == "--install" ]]; then
            install
        elif [[ "$flag" == "--start" ]]; then
            start
        elif [[ "$flag" == "--check" ]]; then
            check_status
        elif [[ "$flag" == "--stop" ]]; then
            stop
        elif [[ "$flag" == "--restart" ]]; then
            restart
        elif [[ "$flag" == "--daemon-reload" ]]; then
            daemon_reload
        else
            unknown_flag $flag
        fi
        shift
        continue
    fi

    length=${#flag}
    for ((i = 1; i < length; i++)); do
        case "${flag:$i:1}" in
        h)
            print_mongo_help
            ;;
        i)
            install
            ;;
        s)
            start
            ;;
        c)
            check_status
            check=true
            ;;
        x)
            stop
            ;;
        r)
            restart
            ;;
        d)
            daemon_reload
            ;;
        *)
            unknown_flag ${flag:$i:1}
            ;;
        esac
    done

    shift
done
