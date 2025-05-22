package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class ObjectUtilsTest {

    private static Object[][] objectsForTestPrintAsJSON() throws NoSuchFieldException {
        Field identifierField = ObjectUtils.class.getDeclaredField("EMPTY_IDENTIFIER");
        Object emptyIdentifier = ReflectionUtils.get(identifierField, ObjectUtils.class)
                .orElseThrow(IllegalStateException::new);
        UUID uuid = UUID.randomUUID();
        Date date = new Date();
        Field dateFormatField = ObjectUtils.class.getDeclaredField("DATE_FORMAT");
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) ReflectionUtils.get(dateFormatField, ObjectUtils.class)
                .orElseThrow(IllegalStateException::new);
        return new Object[][] {
                new Object[]{null, emptyIdentifier},
                new Object[]{BasicEnum.EXAMPLE, BasicEnum.EXAMPLE.name()},
                new Object[]{"Hello world", "\"Hello world\""},
                new Object[]{10, "10"},
                new Object[]{-10, "-10"},
                new Object[]{new Character('c'), "c"},
                new Object[]{Arrays.asList(1, 2, 3), "[1, 2, 3]"},
                new Object[]{uuid, uuid.toString()},
                new Object[]{date, simpleDateFormat.format(date)},
                new Object[]{new HashMap<Integer, Object>(){{
                    put(1, true);
                    put(2, false);
                    put(3, true);
                    put(0, 0);
                }}, "{1: true, 2: false, 3: true}"},
                new Object[]{new ExampleClass(), "{\"i\": 10, \"n\": \"Alex\"}"},
                new Object[]{new Object(), emptyIdentifier},
        };
    }

    @ParameterizedTest
    @MethodSource("objectsForTestPrintAsJSON")
    void testPrintAsJSON(Object object, String expected) {
        assertEquals(expected, ObjectUtils.printAsJSON(object));
    }

    private enum BasicEnum {
        EXAMPLE
    }

    private static class ExampleClass {
        private final int i = 10;
        private final String n = "Alex";
        private final double d = 0; // Since d is 0, it should NOT be printed
        private final Object o = null;
    }

    @Test
    void testCopyOfNoInterface() {
        Object current = new CopyNoInterface("hello");
        Object o = ObjectUtils.copy(current);
        assertInstanceOf(CopyNoInterfaceImpl.class, o);
        assertNotEquals(current, o);
    }

    @Test
    void testCopyOfIterable() {
        Object current = new CopyIterable("hello");
        Object o = ObjectUtils.copy(current);
        assertInstanceOf(CopyIterableImpl.class, o);
        assertNotEquals(current, o);
    }

    @Test
    void testCopyOfData() {
        GeneralCopy c1 = new GeneralCopy();
        GeneralCopy c2 = ObjectUtils.copy(c1);
        assertNotEquals(c1.list.getClass(), c2.list.getClass());
        assertIterableEquals(c1.list, c2.list);
        assertNotEquals(c1.array, c2.array);
        assertArrayEquals(c1.array, c2.array);
        assertNotEquals(c1.copiable, c2.copiable);
        assertEquals(c1.map, c2.map);
        // Try removal from the first map
        c1.map.remove("hello");
        assertEquals("world", c2.map.get("hello"));
    }

    @Test
    void testCopyThrowsIllegalArgument() {
        Throwable exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                ObjectUtils.copy(new GeneralCopyException()));
        assertEquals("Everything good", exception.getMessage());
    }

    private static class GeneralCopy {
        Map<String, String> map = new HashMap<String, String>(){{
            put("hello", "world");
        }};
        List<String> list = Arrays.asList("hello", "world");
        String[] array = new String[]{"hello", "world"};
        GeneralCopiable copiable = new GeneralCopiable();
    }

    private static class GeneralCopyException {
        GeneralCopiableException copiable = new GeneralCopiableException();
    }

    private static class GeneralCopiable {

        public GeneralCopiable copy() {
            return new GeneralCopiable();
        }
    }

    private static class GeneralCopiableException {

        public GeneralCopiable copy() {
            throw new IllegalArgumentException("Everything good");
        }
    }

    private static class CopyIterableImpl { }

    private static class CopyIterable implements Iterable<String> {

        public CopyIterable(String s) {

        }

        @Override
        public Iterator<String> iterator() {
            return null;
        }
    }

    private static class CopyNoInterfaceImpl { }

    private static class CopyNoInterface {

        public CopyNoInterface(String s) {

        }
    }
}