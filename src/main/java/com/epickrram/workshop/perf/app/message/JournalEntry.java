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


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static java.nio.ByteBuffer.allocateDirect;

public final class JournalEntry
{
    public static final int ENTRY_MAGIC_NUMBER = 0x55555555;
    public static final int ENTRY_SIZE = 4 + 8 + 8 + 8;

    private final ByteBuffer buffer = allocateDirect(ENTRY_SIZE);

    public void set(final long publisherNanoTime, final long journallerNanoTime, final long deltaNanos)
    {
        buffer.clear();
        buffer.putInt(ENTRY_MAGIC_NUMBER);
        buffer.putLong(publisherNanoTime);
        buffer.putLong(journallerNanoTime);
        buffer.putLong(deltaNanos);
        buffer.flip();
    }

    public void writeTo(final WritableByteChannel destination) throws IOException
    {
        validateForWriting();
        destination.write(buffer);
    }

    public void readFrom(final ReadableByteChannel source) throws IOException
    {
        buffer.clear();
        source.read(buffer);
        buffer.flip();
    }

    public long getPublisherNanoTime()
    {
        return buffer.getLong(4);
    }

    public long getJournallerNanoTime()
    {
        return buffer.getLong(12);
    }

    public long getDeltaNanos()
    {
        return buffer.getLong(20);
    }

    public boolean canRead()
    {
        return buffer.getInt(0) == ENTRY_MAGIC_NUMBER && buffer.limit() == ENTRY_SIZE;
    }

    private void validateForWriting()
    {
        if(buffer.position() != 0 || buffer.limit() != ENTRY_SIZE)
        {
            throw new IllegalStateException("Buffer is in inconsistent state for writing");
        }
    }
}
