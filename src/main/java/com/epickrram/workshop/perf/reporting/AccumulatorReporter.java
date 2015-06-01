package com.epickrram.workshop.perf.reporting;

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


import com.beust.jcommander.JCommander;
import com.epickrram.workshop.perf.config.CommandLineArgs;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.epickrram.workshop.perf.reporting.HistogramReporter.HISTOGRAM_REPORTER;
import static com.epickrram.workshop.perf.support.Histograms.HISTOGRAMS;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public final class AccumulatorReporter
{
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[^0-9]+\\-([0-9]+)\\.enc$");
    private static final int WARMUP_THRESHOLD = 10;

    private final CommandLineArgs commandLineArgs;

    public AccumulatorReporter(final CommandLineArgs commandLineArgs)
    {
        this.commandLineArgs = commandLineArgs;
    }

    public void run() throws IOException
    {
        final List<File> encodedHistogramsGeneratedAfterWarmup = stream(new File(commandLineArgs.getOutputDir()).listFiles()).
                filter((file) -> file.getName().endsWith(".enc")).
                filter(this::isAfterWarmup).collect(toList());

        final Histogram superHistogram = merge(encodedHistogramsGeneratedAfterWarmup);

        HISTOGRAM_REPORTER.writeReport(superHistogram,
                "Accumulator Message Transit Latency (ns)", System.out);
    }

    private Histogram merge(final List<File> encodedHistogramsGeneratedAfterWarmup)
    {
        final Histogram histogram = HISTOGRAMS.createHistogram();

        encodedHistogramsGeneratedAfterWarmup.stream().forEach((file) -> {
            histogram.add(loadHistogram(file));
        });

        return histogram;
    }

    private Histogram loadHistogram(final File file)
    {
        try
        {
            return (Histogram) new HistogramLogReader(file).nextIntervalHistogram();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Could not process encoded histogram", e);
        }
    }

    private boolean isAfterWarmup(final File file)
    {
        final Matcher matcher = NUMBER_PATTERN.matcher(file.getName());
        return matcher.find() && Integer.parseInt(matcher.group(1)) > WARMUP_THRESHOLD;
    }

    public static void main(final String[] args) throws Exception
    {
        final CommandLineArgs commandLineArgs = new CommandLineArgs();
        new JCommander(commandLineArgs).parse(args);

        new AccumulatorReporter(commandLineArgs).run();
    }
}
