package br.ufmg.ppgee.orcslab.upmsp.util;

import java.util.concurrent.TimeUnit;

/**
 * This class works as a stopwatch.
 */
public class Timer {

    private boolean started;
    private boolean paused;
    private long reference;
    private long accumulated;

    /**
     * Default constructor.
     */
    public Timer() {
        this(false);
    }

    /**
     * Constructor.
     * @param start It true, the timer start just after construction. Otherwise,
     *              it will not be automatically started.
     */
    public Timer(boolean start) {
        reset();
        if (start) {
            start();
        }
    }

    public Timer(Timer timer) {
        started = timer.started;
        paused = timer.paused;
        reference = timer.reference;
        accumulated = timer.accumulated;
    }

    /**
     * Start/resume the timer.
     */
    public void start() {
        if (!started) {
            started = true;
            paused = false;
            accumulated = 0L;
            reference = System.nanoTime();
        } else if (paused) {
            reference = System.nanoTime();
            paused = false;
        }
    }

    /**
     * Stop/pause the timer.
     */
    public void stop() {
        if (started && !paused) {
            accumulated += System.nanoTime() - reference;
            paused = true;
        }
    }

    /**
     * Reset the timer.
     */
    public void reset() {
        started = false;
        paused = false;
        reference = 0L;
        accumulated = 0L;
    }

    /**
     * Return the elapsed time in milliseconds.
     * @return The elapsed time im milliseconds.
     */
    public long count() {
        if (started) {
            if (paused) {
                return TimeUnit.NANOSECONDS.toMillis(accumulated);
            } else {
                return TimeUnit.NANOSECONDS.toMillis(accumulated + (System.nanoTime() - reference));
            }
        } else {
            return 0L;
        }
    }

    /**
     * Return the elapsed time.
     * @param unit Unit used to return the elapsed time.
     * @return The elapsed time.
     */
    public long count(TimeUnit unit) {
        if (started) {
            if (paused) {
                return TimeUnit.NANOSECONDS.convert(accumulated, unit);
            } else {
                return TimeUnit.NANOSECONDS.convert(accumulated + (System.nanoTime() - reference), unit);
            }
        } else {
            return 0L;
        }
    }

}
