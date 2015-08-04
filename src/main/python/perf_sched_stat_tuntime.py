__author__ = 'pricem'

import sys

def parse_timestamp(input):
    return int(float(input.split(":")[0]) * 1000000) * 1000

def parse_runtime(input):
    return int(input.split("=")[1])

if len(sys.argv) == 1:
    print "usage: " + sys.argv[0] + " <pid> <input_file>"

last_timestamp = 0

for line in open(sys.argv[2]).readlines():
    if line.find("sched_stat_runtime") > 0 and line.find("pid=" + sys.argv[1]) > 0:
        tokens = line.strip().split(" ")
        timestamp_nanos = parse_timestamp(tokens[3])
        runtime_nanos = parse_runtime(tokens[7])

        if last_timestamp != 0:
            runtime_reporting_delta_nanos = timestamp_nanos - last_timestamp
            nanos_not_on_cpu = runtime_reporting_delta_nanos - runtime_nanos
            # print line
            print tokens[3] + " " + str(nanos_not_on_cpu)

        last_timestamp = timestamp_nanos