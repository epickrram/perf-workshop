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


import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static java.lang.String.format;

public final class HistogramReporter
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private final long executionTimestamp;
    private final String outputDir;

    public HistogramReporter(final long executionTimestamp, final String outputDir)
    {
        this.executionTimestamp = executionTimestamp;
        this.outputDir = outputDir;
    }

    public void writeReport(final Histogram histogram, final PrintStream out,
                            final Set<ReportFormat> reportFormats,
                            final String histogramTitle) throws IOException
    {
        for (final ReportFormat reportFormat : reportFormats)
        {
            switch (reportFormat)
            {
                case LONG:
                    longReport(histogram, histogramTitle, out);
                    break;
                case SHORT:
                    shortReport(histogram, out);
                    break;
                case DETAILED:
                    encodedHistogram(histogram, histogramTitle, out);
                    break;
                default:
                    throw new IllegalStateException("Unknown report format: " + reportFormat);
            }
        }
    }

    private void encodedHistogram(final Histogram histogram, final String histogramTitle, final PrintStream out)
    {
        try
        {
            final File histogramOutputFile = getHistogramOutputFile(outputDir, histogramTitle);
            out.println("Writing full encoded histogram to " + histogramOutputFile.getAbsolutePath());
            try (final PrintStream printStream = new PrintStream(histogramOutputFile))
            {
                new HistogramLogWriter(printStream).outputIntervalHistogram(0, 1, histogram, 1d);
            }
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Failed to write histogram", e);
        }
    }

    private File getHistogramOutputFile(final String rootDir, final String qualifier)
    {
        final LocalDateTime timestamp = LocalDateTime.ofEpochSecond(executionTimestamp / 1000, 0, ZoneOffset.UTC);
        return new File(rootDir, "encoded-result-histogram-" + cleanse(qualifier) +
                "-" + FORMATTER.format(timestamp) + ".report.enc");
    }

    private String cleanse(final String qualifier)
    {
        return qualifier.replaceAll("[ =\\(\\)]", "_");
    }

    private void longReport(final Histogram histogram, final String histogramTitle,
                            final PrintStream out) throws IOException
    {
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.append(format("== %s ==%n", histogramTitle));
        printWriter.append(format("%-8s%20d%n", "mean", (long) histogram.getMean()));
        printWriter.append(format("%-8s%20d%n", "min", histogram.getMinValue()));
        printWriter.append(format("%-8s%20d%n", "50.00%", histogram.getValueAtPercentile(50.0d)));
        printWriter.append(format("%-8s%20d%n", "90.00%", histogram.getValueAtPercentile(90.0d)));
        printWriter.append(format("%-8s%20d%n", "99.00%", histogram.getValueAtPercentile(99.0d)));
        printWriter.append(format("%-8s%20d%n", "99.90%", histogram.getValueAtPercentile(99.9d)));
        printWriter.append(format("%-8s%20d%n", "99.99%", histogram.getValueAtPercentile(99.99d)));
        printWriter.append(format("%-8s%20d%n", "99.999%", histogram.getValueAtPercentile(99.999d)));
        printWriter.append(format("%-8s%20d%n", "99.9999%", histogram.getValueAtPercentile(99.9999d)));
        printWriter.append(format("%-8s%20d%n", "max", histogram.getMaxValue()));
        printWriter.append(format("%-8s%20d%n", "count", histogram.getTotalCount()));
        printWriter.append("\n");
        printWriter.flush();
    }

    private void shortReport(final Histogram histogram, final PrintStream out) throws IOException
    {
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.append(format("%d,", histogram.getMinValue()));
        printWriter.append(format("%d,", (long) histogram.getMean()));
        printWriter.append(format("%d,", histogram.getValueAtPercentile(50.0d)));
        printWriter.append(format("%d,", histogram.getValueAtPercentile(90.0d)));
        printWriter.append(format("%d,", histogram.getValueAtPercentile(99.0d)));
        printWriter.append(format("%d,", histogram.getValueAtPercentile(99.9d)));
        printWriter.append(format("%d,", histogram.getValueAtPercentile(99.99d)));
        printWriter.append(format("%d,", histogram.getMaxValue()));
        printWriter.append(format("%d%n", histogram.getTotalCount()));
        printWriter.append("\n");
        printWriter.flush();
    }
}
