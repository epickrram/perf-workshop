__author__ = 'pricem'

import re
import sys

TIMESTAMP_REGEX=".* ([0-9]{1,6}\.[0-9]{6}):.*"
FUNCTION_REGEX=".* function ([^\s]+)"


def parse_timestamp(input):
    return int(float(input) * 1000000) * 1000

def parse_runtime(input):
    return int(input)

if len(sys.argv) == 1:
    print "usage: " + sys.argv[0] + " <input_file>"

last_timestamp = 0
start_of_work = 0

for line in open(sys.argv[1]).readlines():
    if line.find("workqueue_execute_start") > 0:
        timestamp_match = re.search(TIMESTAMP_REGEX, line)
        function_match = re.search(FUNCTION_REGEX, line)

        start_of_work = parse_timestamp(timestamp_match.group(1))
        function = function_match.group(1)
    elif line.find("workqueue_execute_end") > 0:
        timestamp_match = re.search(TIMESTAMP_REGEX, line)
        end_of_work = parse_timestamp(timestamp_match.group(1))
        duration_of_work = end_of_work - start_of_work
        print str(duration_of_work) + " " + function