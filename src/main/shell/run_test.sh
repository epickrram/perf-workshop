#!/bin/bash

if [ "$JAVA_HOME" == "" ]; then
    echo "JAVA_HOME is not set!"
    exit 1
fi

JAVA="$JAVA_HOME/bin/java"
LIB_LOCATION="../../../build/libs/perf-workshop-all-0.0.1.jar"

echo "Executing test with:"
$JAVA -version
DEFAULT_JVM_OPTS="-XX:+UnlockDiagnosticVMOptions -XX:GuaranteedSafepointInterval=600000"
JVM_OPTS="$DEFAULT_JVM_OPTS -XX:+DisableExplicitGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCApplicationStoppedTime -XX:+PrintTenuringDistribution -XX:-UseBiasedLocking"
EXEC_PREFIX=""

echo "JVM_OPTS: $JVM_OPTS"

$EXEC_PREFIX $JAVA -Xmx4g -Xms4g $JVM_OPTS -Xloggc:perf-workshop-gc.log -jar $LIB_LOCATION -i 1000 -w 100 -n 4000 -r DETAILED -r LONG | tee output.log
