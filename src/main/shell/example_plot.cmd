
#set log y
set logscale x
unset xtics

plot './xlabels.dat' with labels center offset 0, 1.5 point, \
'./foo.enc.decoded.hgrm' using 4:1 with lines title "latency breakdown"

pause -1
