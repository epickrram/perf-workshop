
set logscale x
unset xtics
set xlabel "Percentile"
set ylabel "Latency nanoseconds"
set title "Inter-thread latency breakdown"

plot './xlabels.dat' with labels center offset 0, 1.5 point title "", \
'./foo.enc.decoded.hgrm' using 4:1 with lines title "latency"

pause -1
