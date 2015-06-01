package com.epickrram.workshop.perf.support;

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

import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

public enum Histograms
{
    HISTOGRAMS;

    public Histogram createHistogramForArray(final int index)
    {
        return new Histogram(TimeUnit.SECONDS.toNanos(1L), 1);
    }

    public Histogram createHistogram()
    {
        return new Histogram(TimeUnit.SECONDS.toNanos(1L), 1);
    }

    public void safeRecord(final long value, final Histogram histogram)
    {
        histogram.recordValue(min(histogram.getHighestTrackableValue(), value));
    }
}
