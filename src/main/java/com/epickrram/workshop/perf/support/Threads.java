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
        if(cpus != null && cpus.length != 0)
        {
            final BitSet requiredAffinity = cpuListToBitMask(cpus);
            Affinity.setAffinity(requiredAffinity);

            if(!requiredAffinity.equals(Affinity.getAffinity()))
            {
                throw new IllegalStateException("Unable to set CPU affinity");
            }

            System.out.println("Set affinity for thread " + Thread.currentThread().getName() + " to " + Arrays.toString(cpus));
        }
    }

    public int getCurrentThreadId()
    {
        return Affinity.getThreadId();
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
