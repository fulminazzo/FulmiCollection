package it.fulminazzo.fulmicollection.utils;

import it.fulminazzo.fulmicollection.objects.PrintableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    static Class<?>[] getTestClasses() {
        return new Class[]{
                ReflectionUtilsTest.class, ReflectionUtils.class,
                Integer.class, String.class, UpperClass.class,
                InnerClass.class, InnerInterface.class
        };
    }

    static Object[][] getPrimitiveClasses() {
        return new Object[][]{
                new Object[]{Class.class, false},
                new Object[]{boolean.class, true},
                new Object[]{byte.class, true},
                new Object[]{short.class, true},
                new Object[]{char.class, true},
                new Object[]{int.class, true},
                new Object[]{long.class, true},
                new Object[]{float.class, true},
                new Object[]{double.class, true},
                new Object[]{String.class, false},
                new Object[]{ReflectionUtils.class, false},
        };
    }

    static Object[][] getConvertedClasses() {
        return new Object[][]{
                new Object[]{Class.class, Class.class},
                new Object[]{boolean.class, boolean.class},
                new Object[]{byte.class, byte.class},
                new Object[]{short.class, short.class},
                new Object[]{char.class, char.class},
                new Object[]{int.class, int.class},
                new Object[]{long.class, long.class},
                new Object[]{float.class, float.class},
                new Object[]{double.class, double.class},
                new Object[]{Boolean.class, boolean.class},
                new Object[]{Byte.class, byte.class},
                new Object[]{Short.class, short.class},
                new Object[]{Character.class, char.class},
                new Object[]{Integer.class, int.class},
                new Object[]{Long.class, long.class},
                new Object[]{Float.class, float.class},
                new Object[]{Double.class, double.class},
                new Object[]{String.class, String.class},
                new Object[]{ReflectionUtils.class, ReflectionUtils.class},
        };
    }

    static Object[][] getTestFields() {
        return new Object[][]{
                new Object[]{"field1", new UpperClass(), false},
                new Object[]{"field4", new InnerClass(), false},
                new Object[]{"field5", new UpperClass(), true},
        };
    }

    static Object[][] getTestMethods() throws NoSuchMethodException {
        return new Object[][]{
                new Object[]{InnerClass.class.getDeclaredMethod("firstMethod"),
                        String.class, "firstMethod", new Object[0]},
                new Object[]{UpperClass.class.getDeclaredMethod("secondMethod", String.class),
                        null, "secondMethod", new Object[]{""}},
                new Object[]{UpperClass.class.getDeclaredMethod("thirdMethod", String.class),
                        Integer.class, "thirdMethod", new Object[]{""}},
                new Object[]{UpperClass.class.getDeclaredMethod("thirdMethod", String.class, String.class),
                        Integer.class, "thirdMethod", new Object[]{"", ""}}
        };
    }

    @ParameterizedTest
    @MethodSource("getTestFields")
    void testFields(String field, Object object, boolean isNull) {
        if (isNull) assertThrowsExactly(IllegalArgumentException.class, () -> ReflectionUtils.getField(object, field));
        else assertDoesNotThrow(() -> ReflectionUtils.getField(object, field));
    }

    @Test
    void testPrintFormat() {
        assertDoesNotThrow(() -> ReflectionUtils.getMethod(System.out, null,
                "printf", "Hello, %s!", "world"));
    }

    @Test
    void testEqualFields() {
        PrintableTest.Person person1 = new PrintableTest.Person("Alex", 10, null);
        PrintableTest.Person person2 = new PrintableTest.Person("Alex", 10, null);
        assertTrue(ReflectionUtils.equalsFields(person1, person2));
    }

    @Test
    void testGetFieldNameless() throws NoSuchFieldException {
        assertEquals(InnerClass.class.getDeclaredField("field3"), ReflectionUtils.getFieldNameless(new InnerClass(), String.class.getCanonicalName()));
    }

    @Test
    void testGetFieldNamelessClass() throws NoSuchFieldException {
        assertEquals(InnerClass.class.getDeclaredField("field3"), ReflectionUtils.getField(new InnerClass(), String.class));
    }

    @Test
    void testGetConstructor() throws NoSuchMethodException {
        assertEquals(InnerClass.class.getDeclaredConstructor(), ReflectionUtils.getConstructor(new InnerClass()));
    }

    @Test
    void testGetFields() throws NoSuchFieldException {
        List<Field> expected = new ArrayList<>();
        expected.add(InnerClass.class.getDeclaredField("field3"));
        expected.add(InnerClass.class.getDeclaredField("field4"));
        expected.add(UpperClass.class.getDeclaredField("field2"));
        expected.add(UpperClass.class.getDeclaredField("field1"));
        assertIterableEquals(expected, ReflectionUtils.getFields(new InnerClass()));
    }

    @ParameterizedTest
    @MethodSource("getTestClasses")
    void testGetClass(Class<?> clazz) {
        assertEquals(clazz, ReflectionUtils.getClass(clazz.getCanonicalName()));
    }

    @Test
    void testGetMethods() {
        final List<Method> methods = TestUtils.computeNonObjectMethods(() -> ReflectionUtils.getMethods(new InnerClass()));
        final List<String> expected = Arrays.asList("firstMethod", "secondMethod", "thirdMethod", "thirdMethod");
        assertEquals(expected.size(), methods.size());
        for (int i = 0; i < expected.size(); i++)
            assertEquals(expected.get(i), methods.get(i).getName());
    }

    @ParameterizedTest
    @MethodSource("getTestMethods")
    void testGetMethod(Method expected, Class<?> returnType, String methodName, Object... parameters) {
        final InnerClass innerClass = new InnerClass();
        assertEquals(expected, ReflectionUtils.getMethod(innerClass, returnType, methodName, parameters));
    }

    @Test
    void testNonExistingClass() {
        assertThrows(RuntimeException.class, () -> ReflectionUtils.getClass("non.existing.class"));
    }

    @ParameterizedTest
    @MethodSource("getPrimitiveClasses")
    void testPrimitiveClasses(Class<?> clazz, boolean expected) {
        assertEquals(expected, ReflectionUtils.isPrimitive(clazz));
    }

    @ParameterizedTest
    @MethodSource("getConvertedClasses")
    void testPrimitiveOrWrapperClasses(Class<?> clazz, Class<?> expected) {
        assertEquals(!(expected == Class.class || expected == ReflectionUtils.class),
                ReflectionUtils.isPrimitiveOrWrapper(clazz));
    }

    @ParameterizedTest
    @MethodSource("getConvertedClasses")
    void testConvertedClasses(Class<?> clazz, Class<?> expected) {
        assertEquals(expected, ReflectionUtils.getPrimitiveClass(clazz));
    }
    
    @Test
    void testCompareFields() throws NoSuchFieldException {
        InnerClass c1 = new InnerClass();
        c1.field3 = "Hello";
        InnerClass c2 = new InnerClass();
        c2.field3 = "Hello";
        Field field = InnerClass.class.getDeclaredField("field3");
        assertTrue(ReflectionUtils.compareFields(field, c1, c2));
    }

    @Test
    void testSetAccessibleOrThrow() throws NoSuchFieldException {
        Field expected = InnerClass.class.getDeclaredField("field3");
        Field actual = ReflectionUtils.setAccessibleOrThrow(expected);
        assertEquals(expected, actual);
        assertTrue(actual.isAccessible());
    }

    @Test
    void testGetOrThrow() throws NoSuchFieldException {
        InnerClass clazz = new InnerClass();
        clazz.field3 = "Hello";
        Field field = InnerClass.class.getDeclaredField("field3");
        assertEquals("Hello", ReflectionUtils.getOrThrow(field, clazz));
    }

    static class InnerClass extends UpperClass {
        public String field3;
        private static String field4;

        String firstMethod() {
            return null;
        }
    }

    static class UpperClass {
        public static String field1;
        private String field2;

        void secondMethod(String arg) {

        }

        Integer thirdMethod(String value) {
            return null;
        }

        Integer thirdMethod(String value, String anotherValue) {
            return null;
        }
    }

    interface InnerInterface {

    }
}