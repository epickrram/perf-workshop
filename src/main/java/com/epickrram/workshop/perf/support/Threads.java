package com.epickrram.workshop.perf.support;

//////////////////////////////////////////////////////////////////////////////////
//   Copyright 2015   Mark Price     mark at epickrram.com                      //
//                                                                              //
//   Licensed under the Apache License, Version 2.0 (the "License");            //
//   you may not use this file except in compliance with the License.           //
//   You may obtain a copy of the License at                                    //
//                                                                              //
//       http://www.apache.org/licenses/LICENSE-2.0                             //
//                                                                              //
//   Unless required by applicable law or agreed to in writing, software        //
//   distributed under the License is distributed on an "AS IS" BASIS,          //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   //
//   See the License for the specific language governing permissions and        //
//   limitations under the License.                                             //
//////////////////////////////////////////////////////////////////////////////////


import net.openhft.affinity.Affinity;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;

public enum Threads
{
    THREADS;

    public Runnable runOnCpu(final Runnable task, final int... cpus)
    {
        return () -> {
            setCurrentThreadAffinity(cpus);
            task.run();
        };
    }

    public void setCurrentThreadAffinity(final int... cpus)
    {
        if (cpus.length == 0) {
            return;
        }
        if (cpus.length != 1) {
            throw new UnsupportedOperationException();
        }

        ProcessBuilder builder = new ProcessBuilder("taskset", "-cp",
          Integer.toString(cpus[0]), Integer.toString(getCurrentThreadId()));

        try {
            builder.start().waitFor();
            System.out.println("Set affinity for thread " + Thread.currentThread().getName() + " to " + Arrays.toString(cpus));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentThreadId()
    {
        try
        {
            // TODO: if Java 9, then use ProcessHandle.
            final String pidPorpertyValue = System.getProperty("sun.java.launcher.pid");

            if (null != pidPorpertyValue)
            {
                return Integer.parseInt(pidPorpertyValue);
            }

            final String jvmName = ManagementFactory.getRuntimeMXBean().getName();

            return Integer.parseInt(jvmName.split("@")[0]);
        }
        catch (final Throwable ex)
        {
            return -1;
        }
    }

    public void sleep(final long duration, final TimeUnit unit)
    {
        try
        {
            Thread.sleep(unit.toMillis(duration));
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Interrupted during sleep!");
        }
    }

    private BitSet cpuListToBitMask(final int[] cpus)
    {
        final BitSet bitSet = new BitSet();

        for(int cpu : cpus)
        {
            bitSet.set(cpu);
        }

        return bitSet;
    }
}
