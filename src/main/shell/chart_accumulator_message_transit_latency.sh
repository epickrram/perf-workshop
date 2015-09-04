#!/bin/bash

CHART_FILE="accumulator_message_transit_latency.cmd"
bash ./convert_encoded_histogram.sh

FILES=`ls ./encoded-result-histogram-Accumulator_Message_Transit_Latency__ns_*.report.enc.decoded.hgrm`

echo "set logscale x" > $CHART_FILE
echo "unset xtics" >> $CHART_FILE
echo "set xlabel \"Percentile\"" >> $CHART_FILE
echo "set ylabel \"Latency nanoseconds\"" >> $CHART_FILE
echo "set title \"Inter-thread latency breakdown\"" >> $CHART_FILE
echo "plot \\" >> $CHART_FILE

for f in $FILES; do
    LABEL=`echo $f | grep -oE "LABEL_([^\.]+)"`
    echo "'./$f' using 4:1 with lines title \"$LABEL\", \\" >> $CHART_FILE
done

echo "'./xlabels.dat' with labels center offset 0, 1.5 point title \"\"" >> $CHART_FILE
echo "pause -1" >> $CHART_FILE

