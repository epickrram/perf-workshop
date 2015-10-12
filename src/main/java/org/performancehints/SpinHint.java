package org.performancehints;

/**
 * COPIED FROM
 * https://github.com/giltene/GilExamples/blob/master/SpinHintTest/src/main/java/org/performancehints/SpinHint.java
 */
public final class SpinHint
{
    // sole ctor
    private SpinHint() {}

    /**
     * Provides a hint to the processor that the code sequence is a spin-wait loop.
     */
    public static void spinLoopHint() {
        // intentionally empty
    }
}
