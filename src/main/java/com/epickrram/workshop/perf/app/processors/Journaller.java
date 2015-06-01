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


import com.epickrram.workshop.perf.config.CommandLineArgs;
import com.epickrram.workshop.perf.app.message.JournalEntry;
import com.epickrram.workshop.perf.app.message.Packet;
import com.epickrram.workshop.perf.support.NanoTimer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.ByteBuffer.allocateDirect;

public final class Journaller
{
    private final NanoTimer nanoTimer;
    private final CommandLineArgs commandLineArgs;
    private final JournalEntry journalEntry = new JournalEntry();
    private final boolean enabled;

    private FileChannel channel;
    private long position = 0;

    public Journaller(final NanoTimer nanoTimer, final CommandLineArgs commandLineArgs, final boolean enabled)
    {
        this.nanoTimer = nanoTimer;
        this.commandLineArgs = commandLineArgs;
        this.enabled = enabled;
    }

    public void init() throws IOException
    {
        final File journalFile = new File(commandLineArgs.getJournalFile());
        if(!journalFile.exists())
        {
            journalFile.createNewFile();
        }
        final RandomAccessFile randomAccessFile = new RandomAccessFile(commandLineArgs.getJournalFile(), "rw");
        channel = randomAccessFile.getChannel();
        channel.truncate(0L);
        final int totalNumberOfJournalEntries = commandLineArgs.getNumberOfRecords() * commandLineArgs.getNumberOfIterations();
        final long expectedMaximumJournalLength = JournalEntry.ENTRY_SIZE * totalNumberOfJournalEntries;
        channel.truncate(expectedMaximumJournalLength);
        final ByteBuffer nullEntry = allocateDirect(JournalEntry.ENTRY_SIZE);

        for(int i = 0; i < totalNumberOfJournalEntries; i++)
        {
            nullEntry.clear();
            channel.position(i * JournalEntry.ENTRY_SIZE);
            channel.write(nullEntry);
        }

        channel.force(false);
    }

    public void process(final Packet packet)
    {
        if(!enabled)
        {
            return;
        }

        if(packet.getSequenceInFile() != 0)
        {
            final long nanoTime = nanoTimer.nanoTime();
            final long deltaNanos = nanoTime - packet.getReceivedNanoTime();
            writeEntry(nanoTime, deltaNanos);

            position += JournalEntry.ENTRY_SIZE;
        }

        if(packet.isLastInStream())
        {
            closeChannel();
        }
    }

    private void writeEntry(final long nanoTime, final long deltaNanos)
    {
        try
        {
            journalEntry.set(nanoTime, deltaNanos);
            channel.position(position);
            journalEntry.writeTo(channel);
        }
        catch (IOException e)
        {
            throw new RuntimeException("File operation failed", e);
        }
    }

    private void closeChannel()
    {
        try
        {
            channel.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("File operation failed", e);
        }
    }
}
