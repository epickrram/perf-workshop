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

    == Accumulator Message Transit Latency ==
    min                    108
    50.00%                 135
    90.00%                 159
    99.00%                 271
    99.90%                 511
    99.99%                 831
    max                  19455
    count                89991
