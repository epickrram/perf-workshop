#!/bin/bash

if [ "$1" == "" ]; then
    echo "Supply cpu list as first argument!"
    exit
fi

for i in `ps aux | grep watchdog | grep -v grep | awk '{print $2}'`; do taskset -cp $1 $i; done