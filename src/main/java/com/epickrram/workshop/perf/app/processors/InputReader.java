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
import com.epickrram.workshop.perf.config.CommandLineArgs;
import com.epickrram.workshop.perf.support.NanoTimer;
import com.lmax.disruptor.RingBuffer;
import org.performancehints.SpinHint;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.epickrram.workshop.perf.support.Threads.THREADS;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.StandardOpenOption.READ;

public final class InputReader
{
    private final RingBuffer<Packet> messageSink;
    private final NanoTimer nanoTimer;
    private final CommandLineArgs commandLineArgs;
    private final int numberOfIterations;

    public InputReader(final RingBuffer<Packet> messageSink, final NanoTimer nanoTimer, final CommandLineArgs commandLineArgs)
    {
        this.messageSink = messageSink;
        this.nanoTimer = nanoTimer;
        this.commandLineArgs = commandLineArgs;
        this.numberOfIterations = commandLineArgs.getNumberOfIterations();
    }

    public void processFiles()
    {
        Thread.currentThread().setName("Producer");
        final File inputFile = new File(commandLineArgs.getInputFile());
        try (final FileChannel fileChannel = open(inputFile.toPath(), READ))
        {
            final MappedByteBuffer mappedFile = fileChannel.map(READ_ONLY, 0L, inputFile.length());
            mappedFile.load();

//            System.out.println("Producer thread has pid: " + THREADS.getCurrentThreadId());
            for (int i = 0; i < numberOfIterations; i++)
            {
                if (i == commandLineArgs.getNumberOfWarmups())
                {
                    System.out.println("Warm-up complete at " + new Date());
                    System.out.println("Pausing for 10 seconds...");
                    THREADS.sleep(10L, TimeUnit.SECONDS);
                    System.out.println("Executing test at " + new Date());
                }

                processSingleFile(mappedFile, i == numberOfIterations - 1);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("File operation failed", e);
        }

    }

    private void processSingleFile(final MappedByteBuffer mappedFile, final boolean isLastIteration)
    {
        for (int recordIndex = 0; recordIndex < commandLineArgs.getNumberOfRecords(); recordIndex++)
        {
            if (!messageSink.hasAvailableCapacity(1))
            {
                throw new RuntimeException("RingBuffer full!");
            }
            final long sequence = messageSink.next();

            try
            {
                final Packet packet = messageSink.get(sequence);
                packet.reset();
                packet.setSequenceInFile(recordIndex);
                packet.setSequence(sequence);
                final boolean isLastInFile = recordIndex == commandLineArgs.getNumberOfRecords() - 1;
                packet.setLastInFile(isLastInFile);
                packet.setReceivedNanoTime(nanoTimer.nanoTime());
                packet.setLastInStream(isLastInFile && isLastIteration);
                mappedFile.position(recordIndex * commandLineArgs.getRecordLength());
                mappedFile.limit(mappedFile.position() + commandLineArgs.getRecordLength());
                packet.getPayload().put(mappedFile);
            }
            finally
            {
                messageSink.publish(sequence);
            }
            introduceMessagePublishDelay();
        }
    }

    private void introduceMessagePublishDelay()
    {
        final long stopSpinningAt = System.nanoTime() + TimeUnit.MICROSECONDS.toNanos(10L);
        while (System.nanoTime() < stopSpinningAt)
        {
            SpinHint.spinLoopHint();
        }
    }
}