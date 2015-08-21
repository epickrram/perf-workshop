package com.epickrram.workshop.perf.app.jitter;

import com.epickrram.workshop.perf.support.Threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.epickrram.workshop.perf.support.DaemonThreadFactory.DAEMON_THREAD_FACTORY;
import static java.util.concurrent.Executors.newFixedThreadPool;

public enum Spinners
{
    SPINNERS;

    private static final long BUSY_SPIN_PERIOD_NANOS = TimeUnit.MILLISECONDS.toNanos(3L);
    private static final long SLEEP_PERIOD_NANOS = TimeUnit.MILLISECONDS.toNanos(30L);

    private volatile boolean running = false;

    public void start()
    {
        running = true;
        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService executorService = newFixedThreadPool(processors, DAEMON_THREAD_FACTORY);
        for(int i = 0; i < processors; i++)
        {
            executorService.submit(new Spinner());
        }
    }

    public void stop()
    {
        running = false;
    }

    private class Spinner implements Runnable
    {
        public long counter = 0;

        @Override
        public void run()
        {
            while(running)
            {
                final long stopSpinningAt = System.nanoTime() + BUSY_SPIN_PERIOD_NANOS;
                while(System.nanoTime() < stopSpinningAt)
                {
                    counter++;
                }

                Threads.THREADS.sleep(SLEEP_PERIOD_NANOS, TimeUnit.NANOSECONDS);
            }
        }
    }
}