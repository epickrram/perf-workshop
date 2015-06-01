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


import static com.higherfrequencytrading.affinity.AffinitySupport.setAffinity;

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
            setAffinity(cpuListToBitMask(cpus));
        }
    }

    private long cpuListToBitMask(final int[] cpus)
    {
        long bitMask = 0L;

        for(int cpu : cpus)
        {
            bitMask |= 1L << cpu;
        }

        return bitMask;
    }
}
