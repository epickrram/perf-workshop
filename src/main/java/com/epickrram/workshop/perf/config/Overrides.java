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


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public final class Overrides
{
    private static final String PROPERTY_PREFIX = "perf.workshop.";
    private static final String JOURNALLER_ENABLED_KEY = PROPERTY_PREFIX + "journaller.enabled";
    private static final String THREAD_AFFINITY_PREFIX = PROPERTY_PREFIX + "affinity.";
    private static final String PRODUCER = "producer";
    private static final String JOURNALLER = "journaller";
    private static final String ACCUMULATOR = "accumulator";

    private final String overrideFile;
    private final Properties properties = new Properties();

    public Overrides(final CommandLineArgs commandLineArgs)
    {
        this.overrideFile = commandLineArgs.getOverrideFile();
    }

    public void init() throws IOException
    {
        final File file = new File(overrideFile);
        if(!file.exists())
        {
            System.out.println(format("Cannot find overrides file, creating one for you at %s", overrideFile));
            properties.setProperty(threadAffinityKeyFor(PRODUCER), "");
            properties.setProperty(threadAffinityKeyFor(JOURNALLER), "");
            properties.setProperty(threadAffinityKeyFor(ACCUMULATOR), "");
            properties.setProperty(JOURNALLER_ENABLED_KEY, Boolean.toString(true));

            try(final FileWriter writer = new FileWriter(file, false))
            {
                properties.store(writer, "override properties for perf workshop");
            }
        }
        else
        {
            System.out.println(format("Loading overrides file from %s", overrideFile));
            try (final FileReader reader = new FileReader(file))
            {
                properties.load(reader);
            }

            ensurePropertiesArePresent(JOURNALLER_ENABLED_KEY, threadAffinityKeyFor(PRODUCER),
                    threadAffinityKeyFor(JOURNALLER), threadAffinityKeyFor(ACCUMULATOR));
        }
    }

    public int[] getProducerThreadAffinity()
    {
        return getThreadAffinity(PRODUCER);
    }

    public int[] getJournallerThreadAffinity()
    {
        return getThreadAffinity(JOURNALLER);
    }

    public int[] getAccumulatorThreadAffinity()
    {
        return getThreadAffinity(ACCUMULATOR);
    }

    public boolean enableJournaller()
    {
        return Boolean.valueOf(properties.getProperty(JOURNALLER_ENABLED_KEY));
    }

    private void ensurePropertiesArePresent(final String... requiredKeys)
    {
        stream(requiredKeys).forEach((key) -> {
            if(!properties.stringPropertyNames().contains(key))
            {
                throw new IllegalStateException(
                        format("Required property %s not found! Try deleting the override file, and it will be regenerated",
                                key));
            }
        });
    }

    private int[] getThreadAffinity(final String threadName)
    {
        final String threadAffinity = properties.getProperty(threadAffinityKeyFor(threadName));

        if(threadAffinity != null && !"".equals(threadAffinity))
        {
            final List<Integer> cpuSet = stream(threadAffinity.split(",")).
                    map(Integer::parseInt).collect(toList());

            final int[] cpus = new int[cpuSet.size()];
            for(int i = 0; i < cpuSet.size(); i++)
            {
                cpus[i] = cpuSet.get(i);
            }

            return cpus;
        }

        return new int[0];
    }

    private static String threadAffinityKeyFor(final String threadName)
    {
        return THREAD_AFFINITY_PREFIX + threadName;
    }
}