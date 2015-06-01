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
import com.epickrram.workshop.perf.app.message.Packet;
import com.epickrram.workshop.perf.support.NanoTimer;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static com.epickrram.workshop.perf.support.Histograms.HISTOGRAMS;
import static java.lang.Math.min;

public final class Accumulator
{
    private final Histogram[] histograms;
    private final NanoTimer nanoTimer;
    private final CommandLineArgs commandLineArgs;

    private int streamNumber = 0;

    public Accumulator(final Histogram[] histograms, final NanoTimer nanoTimer, final CommandLineArgs commandLineArgs)
    {
        this.histograms = histograms;
        this.nanoTimer = nanoTimer;
        this.commandLineArgs = commandLineArgs;
    }

    public void process(final Packet packet)
    {
        if(packet.getSequenceInFile() != 0)
        {
            final long deltaNanos = nanoTimer.nanoTime() - packet.getReceivedNanoTime();
            HISTOGRAMS.safeRecord(deltaNanos, histograms[streamNumber]);
        }

        if(packet.isLastInFile())
        {
            streamNumber++;
        }

        if(packet.isLastInStream())
        {
            for(int i = 0; i < histograms.length; i++)
            {
                outputHistogram(histograms[i], i);
            }
        }
    }

    private void outputHistogram(final Histogram histogram, final int streamNumber)
    {
        try
        {
            try(final PrintStream printStream = new PrintStream(getHistogramOutputFile(commandLineArgs, streamNumber)))
            {
                new HistogramLogWriter(printStream).outputIntervalHistogram(streamNumber, streamNumber + 1, histogram, 1d);
            }
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Failed to write histogram", e);
        }
    }

    private static File getHistogramOutputFile(final CommandLineArgs commandLineArgs, final int streamNumber)
    {
        return new File(commandLineArgs.getOutputDir(), "perf-workshop-accumulator-histogram-" + streamNumber + ".enc");
    }
}
