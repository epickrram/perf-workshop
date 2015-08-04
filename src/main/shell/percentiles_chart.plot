set log y
set datafile separator ','
plot "percentiles_data.csv" using 0:1 title "min", "" using 0:2 title "50%", "" using 0:3 title "90%", "" using 0:4 title "99%", "" using 0:5 title "99.9%", "" using 0:6 title "99.99%", "" using 0:7 title "max"

pause -1
