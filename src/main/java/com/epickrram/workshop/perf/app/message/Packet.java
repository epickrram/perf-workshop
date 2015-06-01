package com.epickrram.workshop.perf.app.message;

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


import com.lmax.disruptor.EventFactory;

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.allocateDirect;

public final class Packet
{
    private final ByteBuffer payload;
    private boolean isLastInFile;
    private long sequence;
    private int sequenceInFile;
    private boolean isLastInStream;
    private long receivedNanoTime;

    private Packet(final int payloadLength)
    {
        this.payload = allocateDirect(payloadLength);
    }

    public void reset()
    {
        isLastInFile = false;
        isLastInStream = false;
        sequence = -1L;
        sequenceInFile = -1;
        receivedNanoTime = 0L;
        payload.clear();
    }

    public ByteBuffer getPayload()
    {
        return payload;
    }

    public boolean isLastInFile()
    {
        return isLastInFile;
    }

    public void setLastInFile(final boolean isLastInFile)
    {
        this.isLastInFile = isLastInFile;
    }

    public long getSequence()
    {
        return sequence;
    }

    public void setSequence(final long sequence)
    {
        this.sequence = sequence;
    }

    public int getSequenceInFile()
    {
        return sequenceInFile;
    }

    public void setSequenceInFile(final int sequenceInFile)
    {
        this.sequenceInFile = sequenceInFile;
    }

    public boolean isLastInStream()
    {
        return isLastInStream;
    }

    public void setLastInStream(final boolean isLastInStream)
    {
        this.isLastInStream = isLastInStream;
    }

    public long getReceivedNanoTime()
    {
        return receivedNanoTime;
    }

    public void setReceivedNanoTime(final long receivedNanoTime)
    {
        this.receivedNanoTime = receivedNanoTime;
    }

    public static final class Factory implements EventFactory<Packet>
    {
        private final int payloadLength;

        public Factory(final int payloadLength)
        {
            this.payloadLength = payloadLength;
        }

        @Override
        public Packet newInstance()
        {
            return new Packet(payloadLength);
        }
    }
}
