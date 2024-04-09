package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadUtilsTest {

    @Test
    void testNullAction() {
        assertDoesNotThrow(() -> ThreadUtils.sleepAndThen(50, null));
    }

    @Test
    void testInterruptedException() {
        ThreadUtils.sleepAndThen(2000, null);
        Thread thread = Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getState().equals(Thread.State.TIMED_WAITING))
                .filter(t -> t.getName().startsWith("Sleep-Then"))
                .findFirst().orElse(null);

        assertNotNull(thread, "Could not find sleeping thread");
        assertDoesNotThrow(thread::interrupt);
    }

}