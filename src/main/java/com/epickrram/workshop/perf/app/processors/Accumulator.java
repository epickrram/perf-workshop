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
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static com.epickrram.workshop.perf.support.Histograms.HISTOGRAMS;

public final class Accumulator
{
    public static final String TRANSIT_TIME_HISTOGRAM_QUALIFIER = "transit-time";
    public static final String INTER_MESSAGE_HISTOGRAM_QUALIFIER = "inter-message";

    private final Histogram[] messageTransitTimeHistograms;
    private final Histogram[] interMessageTimeHistograms;
    private final NanoTimer nanoTimer;
    private final CommandLineArgs commandLineArgs;

    private int streamNumber = 0;
    private long previousMessageNanoTime;

    private boolean setThreadName = false;

    public Accumulator(final Histogram[] messageTransitTimeHistograms, final Histogram[] interMessageTimeHistograms,
                       final NanoTimer nanoTimer, final CommandLineArgs commandLineArgs)
    {
        this.messageTransitTimeHistograms = messageTransitTimeHistograms;
        this.interMessageTimeHistograms = interMessageTimeHistograms;
        this.nanoTimer = nanoTimer;
        this.commandLineArgs = commandLineArgs;
    }

    public void process(final Packet packet)
    {
        if (!setThreadName) {
            Thread.currentThread().setName("Accumulator");
            setThreadName = true;
        }
        final long nanoTime = nanoTimer.nanoTime();
        if(packet.getSequenceInFile() != 0)
        {
            final long deltaNanos = nanoTime - packet.getReceivedNanoTime();
            HISTOGRAMS.safeRecord(deltaNanos, messageTransitTimeHistograms[streamNumber]);
            HISTOGRAMS.safeRecord(nanoTime - previousMessageNanoTime, interMessageTimeHistograms[streamNumber]);
        }

        if(packet.isLastInFile())
        {
            streamNumber++;
        }

        if(packet.isLastInStream())
        {
            outputHistogram(mergeHistogramsAfterWarmupPeriod(messageTransitTimeHistograms), 0, TRANSIT_TIME_HISTOGRAM_QUALIFIER);
            outputHistogram(mergeHistogramsAfterWarmupPeriod(interMessageTimeHistograms), 0, INTER_MESSAGE_HISTOGRAM_QUALIFIER);
        }

        previousMessageNanoTime = nanoTime;
    }

    private Histogram mergeHistogramsAfterWarmupPeriod(final Histogram[] histograms)
    {
        final Histogram target = HISTOGRAMS.createHistogram();
        for (int i = 0; i < histograms.length; i++)
        {
            if(i > commandLineArgs.getNumberOfWarmups())
            {
                target.add(histograms[i]);
            }
        }

        return target;
    }

    private void outputHistogram(final Histogram histogram, final int streamNumber, final String qualifier)
    {
        try
        {
            try(final PrintStream printStream = new PrintStream(getHistogramOutputFile(commandLineArgs, streamNumber, qualifier)))
            {
                new HistogramLogWriter(printStream).outputIntervalHistogram(streamNumber, streamNumber + 1, histogram, 1d);
            }
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Failed to write histogram", e);
        }
    }

    private static File getHistogramOutputFile(final CommandLineArgs commandLineArgs, final int streamNumber, final String qualifier)
    {
        return new File(commandLineArgs.getOutputDir(), "perf-workshop-" +
                "accumulator-histogram-" + qualifier + "-" + streamNumber + ".enc");
    }
}
