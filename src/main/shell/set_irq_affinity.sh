#!/bin/bash

if [ "$1" == "" ]; then
    echo "Supply cpu list as first argument!"
    exit
fi

for i in `ls /proc/irq/ | grep -v default`; do echo "$1" > /proc/irq/$i/smp_affinity_list ; done
