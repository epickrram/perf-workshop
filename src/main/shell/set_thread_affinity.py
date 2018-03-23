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
        print "Application pid: " + str(pid)
        cpu_index = 1
        for thread in pids.split("\n"):
            if thread.find("nid=0x") > -1:
                tokens = thread.strip().split(" ")
                for token in tokens:
                    if token.find("nid=0x") != -1:
                        tid = token.split("=")
                        if tokens[0].find("Producer") != -1 or tokens[0].find("Accumulator") != -1:
                            # do taskset
                            cpu = sys.argv[cpu_index]
                            affined_tid = str(int(str(tid[1]), 16))
                            subprocess.check_output(["taskset", "-cp", cpu, affined_tid])
                            cpu_index += 1
                            print tokens[0] + " has pid " + affined_tid
                            print "Set " + tokens[0] + "(" + affined_tid + ") to run on CPU" + cpu
                        elif tokens[0].find("Journaller") != -1:
                            print "\"Journaller\" has pid " + affined_tid
                    

