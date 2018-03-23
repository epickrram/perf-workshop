#!/usr/bin/python

import os
import subprocess
import sys
import time

counts=dict()

if os.environ["JAVA_HOME"] is None:
    raise RuntimeError

result = subprocess.check_output([os.environ["JAVA_HOME"] + "/bin/jps"])
for line in result.split("\n"):
    if line.find("Jps") < 0 and len(line.strip()) != 0 and line.find("perf-workshop-all-0.0.1.jar") != -1:
        pid = line.split(" ")[0].strip()
        pids = subprocess.check_output(([os.environ["JAVA_HOME"] + "/bin/jstack", str(pid)]))
        for thread in pids.split("\n"):
            if thread.find("nid=0x") > -1:
                tokens = thread.strip().split(" ")
                for token in tokens:
                    if token.find("nid=0x") != -1:
                        tid = token.split("=")
                        if tokens[0].find("Producer") != -1 or tokens[0].find("Accumulator") != -1:
                            print tokens[0] + " " + str(int(str(tid[1]), 16))
                    

