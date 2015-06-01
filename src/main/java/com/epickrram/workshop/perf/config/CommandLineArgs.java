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

import java.io.File;

import static java.lang.System.getProperty;

public final class CommandLineArgs
{
    @Parameter(names = "-i", description = "number of iterations")
    private int numberOfIterations = 20;
    @Parameter(names = "-r", description = "number of records per input file")
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
}
