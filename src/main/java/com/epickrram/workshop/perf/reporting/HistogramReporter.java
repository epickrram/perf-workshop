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

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import static java.lang.String.format;

public enum HistogramReporter
{
    HISTOGRAM_REPORTER;

    public void writeReport(final Histogram histogram, final String histogramTitle,
                            final PrintStream out) throws IOException
    {
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.append(format("== %s ==%n", histogramTitle));
        printWriter.append(format("%-6s%20d%n", "min", histogram.getMinValue()));
        printWriter.append(format("%-6s%20d%n", "50.00%", histogram.getValueAtPercentile(50.0d)));
        printWriter.append(format("%-6s%20d%n", "90.00%", histogram.getValueAtPercentile(90.0d)));
        printWriter.append(format("%-6s%20d%n", "99.00%", histogram.getValueAtPercentile(99.0d)));
        printWriter.append(format("%-6s%20d%n", "99.90%", histogram.getValueAtPercentile(99.9d)));
        printWriter.append(format("%-6s%20d%n", "99.99%", histogram.getValueAtPercentile(99.99d)));
        printWriter.append(format("%-6s%20d%n", "max", histogram.getMaxValue()));
        printWriter.append(format("%-6s%20d%n", "count", histogram.getTotalCount()));
        printWriter.append("\n");
        printWriter.flush();
    }

    public void shortReport(final Histogram histogram, final PrintStream out) throws IOException
    {
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.append(format("%d,", histogram.getMinValue()));
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
