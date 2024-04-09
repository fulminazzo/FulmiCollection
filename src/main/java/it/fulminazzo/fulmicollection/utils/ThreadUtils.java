package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.Nullable;

/**
 * An utils class to interact with {@link Thread}s.
 */
public class ThreadUtils {
    private static final String SLEEP_THEN_NAME = "Sleep-Then-%s";
    private static long sleepAndThenCounter = 0;

    /**
     * Waits for the specified milliseconds, then executes the given action.
     *
     * @param milliseconds the milliseconds
     * @param action       the action
     */
    protected static void sleepAndThen(long milliseconds, final @Nullable Runnable action) {
        sleepAndThen(milliseconds, action, null);
    }

    /**
     * Waits for the specified milliseconds, then executes the given action.
     *
     * @param milliseconds the milliseconds
     * @param action       the action
     * @param onInterrupt  the action to run in case of an {@link InterruptedException}
     */
    protected static void sleepAndThen(long milliseconds, final @Nullable Runnable action,
                                       final @Nullable Runnable onInterrupt) {
        new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                if (onInterrupt != null) onInterrupt.run();
                return;
            }
            if (action != null) action.run();
        }, String.format(SLEEP_THEN_NAME, sleepAndThenCounter++)).start();
    }

}
