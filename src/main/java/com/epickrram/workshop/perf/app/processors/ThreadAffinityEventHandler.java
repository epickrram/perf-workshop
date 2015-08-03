package com.epickrram.workshop.perf.app.processors;

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


import com.epickrram.workshop.perf.app.message.Packet;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import net.openhft.affinity.Affinity;

import static com.epickrram.workshop.perf.support.Threads.THREADS;

public final class ThreadAffinityEventHandler implements EventHandler<Packet>, LifecycleAware
{
    private final EventHandler<Packet> delegate;
    private final String processName;
    private final int[] cpuAffinity;

    private ThreadAffinityEventHandler(final EventHandler<Packet> delegate, final String processName, final int... cpuAffinity)
    {
        this.delegate = delegate;
        this.processName = processName;
        this.cpuAffinity = cpuAffinity;
    }

    @Override
    public void onEvent(final Packet event, final long sequence, final boolean endOfBatch) throws Exception
    {
        delegate.onEvent(event, sequence, endOfBatch);
    }

    @Override
    public void onStart()
    {
        System.out.println(processName + " thread has pid: " + THREADS.getCurrentThreadId());
        if(cpuAffinity != null && cpuAffinity.length != 0)
        {
            THREADS.setCurrentThreadAffinity(cpuAffinity);
        }
    }

    public static EventHandler<Packet> runOnCpus(final EventHandler<Packet> delegate, final String processName, final int... cpus)
    {
        return new ThreadAffinityEventHandler(delegate, processName, cpus);
    }

    @Override
    public void onShutdown()
    {

    }
}
