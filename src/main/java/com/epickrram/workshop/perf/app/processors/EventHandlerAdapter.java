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

import java.util.function.Consumer;

public final class EventHandlerAdapter implements EventHandler<Packet>
{
    private final Consumer<Packet> consumer;

    private EventHandlerAdapter(final Consumer<Packet> consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(final Packet event, final long sequence, final boolean endOfBatch) throws Exception
    {
        consumer.accept(event);
    }

    public static EventHandler<Packet> wrap(final Consumer<Packet> consumer)
    {
        return new EventHandlerAdapter(consumer);
    }
}
