package it.fulminazzo.fulmicollection.structures;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CacheMapTest {

    @Test
    void testExpired() throws InterruptedException {
        final String key = "Hello";
        Map<String, Integer> map = new CacheMap<>(10, 50);
        map.put(key, 10);
        assertNotNull(map.get(key), "Cache should have value after put");
        Thread.sleep(20);
        assertNotNull(map.get(key), "Cache should have value after check");
        Thread.sleep(30);
        assertNull(map.get(key), "Cache should not have value after expire");
    }
}