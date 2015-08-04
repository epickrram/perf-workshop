__author__ = 'pricem'

import re
import sys

TIMESTAMP_REGEX=".* ([0-9]{1,6}\.[0-9]{6}):.*"
RUNTIME_REGEX=".* runtime=([0-9]+) "

def parse_timestamp(input):
    return int(float(input) * 1000000) * 1000

def parse_runtime(input):
    return int(input)

if len(sys.argv) == 1:
    print "usage: " + sys.argv[0] + " <pid> <input_file>"

last_timestamp = 0

for line in open(sys.argv[2]).readlines():
    if line.find("sched_stat_runtime") > 0 and line.find("pid=" + sys.argv[1]) > 0:
        timestamp_match = re.search(TIMESTAMP_REGEX, line)
        runtime_match = re.search(RUNTIME_REGEX, line)

        timestamp_nanos = parse_timestamp(timestamp_match.group(1))
        runtime_nanos = parse_runtime(runtime_match.group(1))

        if last_timestamp != 0:
            runtime_reporting_delta_nanos = timestamp_nanos - last_timestamp
            nanos_not_on_cpu = runtime_reporting_delta_nanos - runtime_nanos
            print str(timestamp_nanos) + " " + str(nanos_not_on_cpu)

        last_timestamp = timestamp_nanos