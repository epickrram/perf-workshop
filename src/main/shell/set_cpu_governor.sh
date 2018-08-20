#!/bin/bash

if [ "$1" == "" ]; then
    echo "Supply governor [powersave|performance] as first argument!"
    exit
fi


for i in `ls /sys/devices/system/cpu/ | grep "cpu[0-9]" | grep -v idle`; do echo $1 > /sys/devices/system/cpu/$i/cpufreq/scaling_governor ;done
