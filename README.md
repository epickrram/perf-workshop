System Jitter Utility
=====================

A test program for exploring causes of jitter.


Using
=====

1. Clone this git repository
2. Build the library: `./gradlew bundleJar`
3. Run it: `java -jar build/libs/perf-workshop-all-0.0.1.jar`


Output
======

    == Accumulator Message Transit Latency (ns) ==
    min                    136
    50.00%                 175
    90.00%                 191
    99.00%                 247
    99.90%                 319
    99.99%                 543
    max                  11263
    count                89991

