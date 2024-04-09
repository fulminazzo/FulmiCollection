package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ThreadUtilsTest {

    @Test
    void testSimpleAction() throws InterruptedException {
        AtomicBoolean bool = new AtomicBoolean(false);
        ThreadUtils.sleepAndThen(50, () -> bool.set(true));
        Thread.sleep(100);
        assertTrue(bool.get());
    }

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