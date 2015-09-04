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
import com.epickrram.workshop.perf.app.processors.Accumulator;
import com.epickrram.workshop.perf.config.CommandLineArgs;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

import static com.epickrram.workshop.perf.support.Histograms.HISTOGRAMS;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public final class AccumulatorReporter
{
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[^0-9]+\\-([0-9]+)\\.enc$");

    private final CommandLineArgs commandLineArgs;

    public AccumulatorReporter(final CommandLineArgs commandLineArgs)
    {
        this.commandLineArgs = commandLineArgs;
    }

    public void run() throws IOException
    {
        reportHistogram("Accumulator Inter-Message Latency (ns)", Accumulator.INTER_MESSAGE_HISTOGRAM_QUALIFIER);
        reportHistogram("Accumulator Message Transit Latency (ns)", Accumulator.TRANSIT_TIME_HISTOGRAM_QUALIFIER);
    }

    public void cleanUp() throws IOException
    {
        stream(new File(commandLineArgs.getOutputDir()).listFiles()).
                filter((file) -> file.getName().endsWith(".enc")).
                filter((file) -> !file.getName().contains("report")).
                forEach((file) -> {

                    try
                    {
                        Files.delete(file.toPath());
                    }
                    catch (IOException e)
                    {
                        // ignore
                    }
                });
    }

    private void reportHistogram(final String histogramTitle, final String histogramQualifier) throws IOException
    {
        final List<File> encodedHistogramsGeneratedAfterWarmup = stream(new File(commandLineArgs.getOutputDir()).listFiles()).
                filter((file) -> file.getName().endsWith(".enc")).
                filter((file) -> {
                    return file.getName().contains(histogramQualifier);
                }).
                collect(toList());

        final Histogram superHistogram = merge(encodedHistogramsGeneratedAfterWarmup);

        new HistogramReporter(commandLineArgs.getExecutionTimestamp(), commandLineArgs.getOutputDir(),
                commandLineArgs.getTestLabel()).writeReport(superHistogram, System.out, commandLineArgs.getReportFormats(), histogramTitle);
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

    public static void main(final String[] args) throws Exception
    {
        final CommandLineArgs commandLineArgs = new CommandLineArgs();
        new JCommander(commandLineArgs).parse(args);

        final AccumulatorReporter accumulatorReporter = new AccumulatorReporter(commandLineArgs);
        try
        {
            accumulatorReporter.run();
        }
        finally
        {
            accumulatorReporter.cleanUp();
        }
    }
}
