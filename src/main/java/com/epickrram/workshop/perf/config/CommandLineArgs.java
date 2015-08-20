package com.epickrram.workshop.perf.config;

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


import com.beust.jcommander.Parameter;
import com.epickrram.workshop.perf.reporting.ReportFormat;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;

public final class CommandLineArgs
{
    private final long executionTimestamp = System.currentTimeMillis();

    @Parameter(names = "-i", description = "number of iterations")
    private int numberOfIterations = 20;
    @Parameter(names = "-w", description = "number of warm-ups")
    private int numberOfWarmups = 10;
    @Parameter(names = "-n", description = "number of records per input file")
    private int numberOfRecords = 10000;
    @Parameter(names = "-l", description = "length of record (bytes)")
    private int recordLength = 64;
    @Parameter(names = "-f", description = "data file (default: /tmp/perf-workshop-input.bin)")
    private String inputFile = getTmpDirectory() + File.separator + "perf-workshop-input.bin";
    @Parameter(names = "-b", description = "buffer size (must be a power of two)")
    private int bufferSize = 524288;
    @Parameter(names = "-j", description = "journal file (default: /tmp/perf-workshop-output.jnl)")
    private String journalFile = getTmpDirectory() + File.separator + "perf-workshop-output.jnl";
    @Parameter(names = "-d", description = "output dir (default: /tmp)")
    private String outputDir = getTmpDirectory();
    @Parameter(names = "-o", description = "overrides file (default: /tmp/perf-workshop.properties)")
    private String overrideFile = getTmpDirectory() + File.separator + "perf-workshop.properties";
    @Parameter(names = "-r", description = "report format", variableArity = true)
    private List<String> reportFormats = asList(ReportFormat.LONG.name());
    @Parameter(names = "-h", description = "print help and exit", help = true)
    private boolean help;

    public int getBufferSize()
    {
        return bufferSize;
    }

    public String getInputFile()
    {
        return inputFile;
    }

    public int getNumberOfIterations()
    {
        return numberOfIterations;
    }

    public int getNumberOfWarmups()
    {
        return numberOfWarmups;
    }

    public int getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public int getRecordLength()
    {
        return recordLength;
    }

    public String getJournalFile()
    {
        return journalFile;
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    private static String getTmpDirectory()
    {
        return getProperty("java.io.tmpdir");
    }

    public String getOverrideFile()
    {
        return overrideFile;
    }

    public boolean isHelp()
    {
        return help;
    }

    public Set<ReportFormat> getReportFormats()
    {
        final EnumSet<ReportFormat> formats = EnumSet.noneOf(ReportFormat.class);
        for (final String reportFormat : reportFormats)
        {
            formats.add(parseReportFormat(reportFormat));
        }
        return formats;
    }

    private ReportFormat parseReportFormat(final String reportFormat)
    {
        try
        {
            return ReportFormat.valueOf(reportFormat);
        }
        catch(final RuntimeException e)
        {
            throw new IllegalArgumentException("Unable to parse report format, must be one of: " +
                    Arrays.toString(ReportFormat.values()));
        }
    }

    public long getExecutionTimestamp()
    {
        return executionTimestamp;
    }
}
