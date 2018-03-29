# gnuplot -e "cols=${COLUMNS}; rows=${LINES}"
unset output
set terminal dumb
#set output "/tmp/output.ascii"
set term dumb size cols, rows
set origin 0,0
set multiplot
set size 1,0.4
set origin 0,0.6


set logscale x
unset xtics
set xlabel "Percentile"
set ylabel "Latency nanoseconds"
set title "Inter-thread latency breakdown"
plot \
'/tmp/encoded-result-histogram-Accumulator_Message_Transit_Latency__ns_-20180327-121054-LABEL_LAPTOP.report.enc.decoded.hgrm' using 4:1 with lines title "LAPTOP", \
'./xlabels.dat' with labels center offset 0, 1.5 point title ""


unset multiplot
unset output
reset
