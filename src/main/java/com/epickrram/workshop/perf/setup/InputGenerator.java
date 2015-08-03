package com.epickrram.workshop.perf.setup;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public final class InputGenerator
{
    private final CommandLineArgs commandLineArgs;

    public InputGenerator(final CommandLineArgs commandLineArgs)
    {
        this.commandLineArgs = commandLineArgs;
    }

    public static void main(final String[] args) throws Exception
    {
        final CommandLineArgs commandLineArgs = new CommandLineArgs();
        new JCommander(commandLineArgs).parse(args);

        new InputGenerator(commandLineArgs).run();

        System.out.printf("Created input journal of %dkb%n", (commandLineArgs.getNumberOfRecords() * commandLineArgs.getRecordLength() / 1024));
    }

    private void run() throws IOException
    {
        final Random random = new Random(0xDEADC0DE);
        final byte[] record = new byte[commandLineArgs.getRecordLength()];
        try(final OutputStream stream = new FileOutputStream(new File(commandLineArgs.getInputFile())))
        {
            for(int recordNumber = 0; recordNumber < commandLineArgs.getNumberOfRecords(); recordNumber++)
            {
                random.nextBytes(record);
                stream.write(record);
            }
        }
    }
}