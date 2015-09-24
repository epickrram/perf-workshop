#!/bin/bash

HISTOGRAM_SOURCE_DIR="/tmp"

if [ "$1" != "" ]; then
    HISTOGRAM_SOURCE_DIR=$1
fi

for i in `ls $HISTOGRAM_SOURCE_DIR/*.report.enc`; do ../../../deps/jHiccup-2.0.5/jHiccupLogProcessor -i $i -o $i.decoded -outputValueUnitRatio 1; done

