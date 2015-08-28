#!/bin/bash

for i in `ls *.report.enc`; do ../../../deps/jHiccup-2.0.5/jHiccupLogProcessor -i $i -o $i.decoded -outputValueUnitRatio 1; done

