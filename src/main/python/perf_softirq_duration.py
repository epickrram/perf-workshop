__author__ = 'pricem'

import re
import sys

TIMESTAMP_REGEX=".* ([0-9]{1,12}\.[0-9]{6}):.*"
CAUSE_REGEX=".*action=([^\]]+)"


def parse_timestamp(input):
    return int(float(input) * 1000000) * 1000

def parse_runtime(input):
    return int(input)

if len(sys.argv) == 1:
    print "usage: " + sys.argv[0] + " <input_file>"

last_timestamp = 0
start_of_irq_entry_by_pid_and_cpu = {}

def get_key_from_tokens(line):
    tokens = re.split("\s+", line.strip())
    key = tokens[1] + "-" + tokens[2]
    return key

for line in open(sys.argv[1]).readlines():
    if line.find("irq:softirq_entry") > 0:
        timestamp_match = re.search(TIMESTAMP_REGEX, line)
        cause_match = re.search(CAUSE_REGEX, line)

        start_of_work = parse_timestamp(timestamp_match.group(1))
        cause = cause_match.group(1)

        key = get_key_from_tokens(line)

        if key in start_of_irq_entry_by_pid_and_cpu:
            print "Did not consume previous entry for: " + line

        start_of_irq_entry_by_pid_and_cpu[key] = [start_of_work, cause]


    elif line.find("irq:softirq_exit") > 0:
        timestamp_match = re.search(TIMESTAMP_REGEX, line)
        end_of_work = parse_timestamp(timestamp_match.group(1))
        key = get_key_from_tokens(line)
        if key not in start_of_irq_entry_by_pid_and_cpu:
            print "Did not record start time for: " + line
        previous_entry = start_of_irq_entry_by_pid_and_cpu[key]
        del start_of_irq_entry_by_pid_and_cpu[key]

        duration_of_work = end_of_work - previous_entry[0]
        print str(int(duration_of_work / 1000)) + "us " + previous_entry[1] + " for pid/cpu: " + key