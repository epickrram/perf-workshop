package com.epickrram.workshop.perf.support;

import com.epickrram.workshop.perf.reporting.HistogramReporter;
import com.epickrram.workshop.perf.reporting.ReportFormat;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogReader;

import java.io.IOException;
import java.util.EnumSet;

public final class EncodedHistogramReporter
{
    public static void main(final String[] args) throws IOException
    {
        final Histogram histogram = (Histogram) new HistogramLogReader(args[0]).nextIntervalHistogram();
        final HistogramReporter reporter = new HistogramReporter(0L, null);
        reporter.writeReport(histogram, System.out, EnumSet.of(ReportFormat.LONG), args[0]);
    }
}