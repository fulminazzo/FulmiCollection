package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReflTest extends AbstractReflTest {

    @Test
    void testEquality() {
        assertEquals(this.refl, new Refl<>(this.testClass));
    }

    @Test
    void testInEquality() {
        assertNotEquals(this.refl, new Refl<>(new Object()));
    }

    @Test
    void testToString() {
        assertEquals(this.testClass.toString(), this.refl.toString());
    }

    @Nested
    @DisplayName("Test methods")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MethodTest extends AbstractReflTest {

        private Object[] getMethod() {
            return new Object[]{
                    (Supplier<Method>) () -> this.refl.getMethod(new Object[]{"param1"}),
                    (Supplier<Method>) () -> this.refl.getMethod(new Class[]{String.class}),
                    (Supplier<Method>) () -> this.refl.getMethod("printField", "param1"),
                    (Supplier<Method>) () -> this.refl.getMethod("printField", String.class),
                    (Supplier<Method>) () -> this.refl.getMethod(String.class, new Object[]{"param1"}),
                    (Supplier<Method>) () -> this.refl.getMethod(String.class, new Class[]{String.class}),
                    (Supplier<Method>) () -> this.refl.getMethod(String.class, "printField", "param1"),
                    (Supplier<Method>) () -> this.refl.getMethod(String.class, "printField", String.class),
            };
        }

        private Object[] invokeMethod() {
            String param1 = "Hello, ";
            return new Object[]{
                    (Supplier<String>) () -> this.refl.invokeMethod(new Object[]{param1}),
                    (Supplier<String>) () -> this.refl.invokeMethod(new Class[]{String.class}, param1),
                    (Supplier<String>) () -> this.refl.invokeMethod("printField", param1),
                    (Supplier<String>) () -> this.refl.invokeMethod("printField", new Class[]{String.class}, param1),
                    (Supplier<String>) () -> this.refl.invokeMethod(String.class, new Object[]{param1}),
                    (Supplier<String>) () -> this.refl.invokeMethod(String.class, new Class[]{String.class}, param1),
                    (Supplier<String>) () -> this.refl.invokeMethod(String.class, "printField", param1),
                    (Supplier<String>) () -> this.refl.invokeMethod(String.class, "printField", new Class[]{String.class}, param1),
            };
        }

        private Object[] callMethod() {
            String param1 = "Hello, ";
            return new Object[]{
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(new Object[]{param1}),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(new Class[]{String.class}, param1),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod("printField", param1),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod("printField", new Class[]{String.class}, param1),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(String.class, new Object[]{param1}),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(String.class, new Class[]{String.class}, param1),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(String.class, "printField", param1),
                    (Supplier<Refl<?>>) () -> this.refl.callMethod(String.class, "printField", new Class[]{String.class}, param1),
            };
        }

        private Object[] invokeMethodRefl() {
            String param1 = "Hello, ";
            return new Object[]{
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(new Object[]{param1}),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(new Class[]{String.class}, param1),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl("printField", param1),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl("printField", new Class[]{String.class}, param1),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(String.class, new Object[]{param1}),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(String.class, new Class[]{String.class}, param1),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(String.class, "printField", param1),
                    (Supplier<Refl<String>>) () -> this.refl.invokeMethodRefl(String.class, "printField", new Class[]{String.class}, param1),
            };
        }

        @ParameterizedTest
        @MethodSource("getMethod")
        void testGetMethod(Supplier<Method> supplier) throws NoSuchMethodException {
            assertEquals(TestClass.class.getDeclaredMethod("printField", String.class), supplier.get());
        }

        @ParameterizedTest
        @MethodSource("invokeMethod")
        void testInvokeMethod(Supplier<String> supplier) {
            assertEquals(this.testClass.printField("Hello, "), supplier.get());
        }

        @ParameterizedTest
        @MethodSource("callMethod")
        void testCallMethod(Supplier<String> supplier) {
            TestClass testClass = mock(TestClass.class);
            this.refl = new Refl<>(testClass);
            supplier.get();
            verify(testClass, atLeastOnce()).printField("Hello, ");
        }

        @ParameterizedTest
        @MethodSource("invokeMethodRefl")
        void testInvokeMethodRefl(Supplier<Refl<String>> supplier) {
            assertEquals(new Refl<>(this.testClass.printField("Hello, ")), supplier.get());
        }
        
        @Test
        void testGetMethods() throws NoSuchMethodException {
            assertIterableEquals(Arrays.asList(
                    TestClass.class.getDeclaredMethod("printField", String.class),
                    TestClass.class.getDeclaredMethod("equals", Object.class),
                    TestClass.class.getDeclaredMethod("print")), this.refl.getMethods());
        }

        @Test
        void testGetStaticMethods() throws NoSuchMethodException {
            assertIterableEquals(Collections.singletonList(TestClass.class.getDeclaredMethod("print")), this.refl.getStaticMethods());
        }

        @Test
        void testGetNonStaticMethods() throws NoSuchMethodException {
            assertIterableEquals(Arrays.asList(
                    TestClass.class.getDeclaredMethod("printField", String.class),
                    TestClass.class.getDeclaredMethod("equals", Object.class)), this.refl.getNonStaticMethods());
        }
    }

    @Nested
    @DisplayName("Test fields")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FieldTest extends AbstractReflTest {

        private Object[] getField() {
            return new Object[]{
                    (Supplier<Field>) () -> this.refl.getFieldNameless(String.class.getCanonicalName()),
                    (Supplier<Field>) () -> this.refl.getField(String.class),
                    (Supplier<Field>) () -> this.refl.getField("name"),
                    (Supplier<Field>) () -> this.refl.getField(f -> f.getName().equalsIgnoreCase("name")),
            };
        }

        private Object[] getFieldObject() {
            return new Object[]{
                    (Supplier<String>) () -> this.refl.getFieldObjectNameless(String.class.getCanonicalName()),
                    (Supplier<String>) () -> this.refl.getFieldObject(String.class),
                    (Supplier<String>) () -> this.refl.getFieldObject(f -> f.getType().equals(String.class)),
                    (Supplier<String>) () -> {
                        try {
                            return this.refl.getFieldObject(TestClass.class.getDeclaredField("name"));
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    },
            };
        }

        private Object[] getFieldRefl() {
            return new Object[]{
                    (Supplier<Refl<String>>) () -> this.refl.getFieldReflNameless(String.class.getCanonicalName()),
                    (Supplier<Refl<String>>) () -> this.refl.getFieldRefl(String.class),
                    (Supplier<Refl<String>>) () -> this.refl.getFieldRefl("name"),
                    (Supplier<Refl<String>>) () -> this.refl.getFieldRefl(f -> f.getType().equals(String.class)),
                    (Supplier<Refl<String>>) () -> {
                        try {
                            return this.refl.getFieldRefl(TestClass.class.getDeclaredField("name"));
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    },
            };
        }

        private Object[] setFieldObject() {
            return new Object[]{
                    (Supplier<Refl<?>>) () -> this.refl.setFieldObjectNameless(String.class.getCanonicalName(), "Peter"),
                    (Supplier<Refl<?>>) () -> this.refl.setFieldObject(String.class, "Peter"),
                    (Supplier<Refl<?>>) () -> this.refl.setFieldObject("name", "Peter"),
                    (Supplier<Refl<?>>) () -> this.refl.setFieldObject(f -> f.getType().equals(String.class), "Peter"),
                    (Supplier<Refl<?>>) () -> {
                        try {
                            return this.refl.setFieldObject(TestClass.class.getDeclaredField("name"), "Peter");
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    },
            };
        }

        @ParameterizedTest
        @MethodSource("getField")
        void testGetField(Supplier<Field> supplier) throws NoSuchFieldException {
            assertEquals(TestClass.class.getDeclaredField("name"), supplier.get());
        }

        @ParameterizedTest
        @MethodSource("getFieldObject")
        void testGetFieldObject(Supplier<String> supplier) {
            assertEquals("James", supplier.get());
        }

        @ParameterizedTest
        @MethodSource("getFieldRefl")
        void testGetFieldRefl(Supplier<Refl<String>> supplier) {
            assertEquals("James", supplier.get().getObject());
        }

        @ParameterizedTest
        @MethodSource("setFieldObject")
        void testSetFieldObject(Supplier<Refl<?>> supplier) throws NoSuchFieldException, IllegalAccessException {
            Field field = TestClass.class.getDeclaredField("name");
            field.setAccessible(true);
            field.set(this.testClass, "James");
            assertEquals(this.refl, supplier.get());
            assertEquals("Peter", this.testClass.name);
        }

        @Test
        void testGetFieldFromNull() {
            assertThrows(IllegalStateException.class, () -> new Refl<>(null).getField("test"));
        }

        @Test
        void testGetFieldObjectFromNull() {
            assertThrows(IllegalStateException.class, () -> new Refl<>(null).getFieldObject("test"));
        }

        @Test
        void testGetReflNull() {
            TestClass testClass1 = new TestClass(null);
            Refl<TestClass> refl1 = new Refl<>(testClass1);
            assertNull(refl1.getFieldRefl("name").getObject());
        }

        @Test
        void testGetFields() throws NoSuchFieldException {
            assertIterableEquals(Arrays.asList(
                    TestClass.class.getDeclaredField("age"),
                    TestClass.class.getDeclaredField("name"),
                    TestClass.class.getDeclaredField("CONSTANT")), this.refl.getFields());
        }

        @Test
        void testGetStaticFields() throws NoSuchFieldException {
            assertIterableEquals(Collections.singletonList(TestClass.class.getDeclaredField("CONSTANT")), this.refl.getStaticFields());
        }

        @Test
        void testGetNonStaticFields() throws NoSuchFieldException {
            assertIterableEquals(Arrays.asList(
                    TestClass.class.getDeclaredField("age"),
                    TestClass.class.getDeclaredField("name")), this.refl.getNonStaticFields());
        }

        @Test
        void testGetFieldsPredicate() throws NoSuchFieldException {
            assertIterableEquals(Collections.singletonList(TestClass.class.getDeclaredField("name")),
                    this.refl.getFields(f -> f.getName().equalsIgnoreCase("name")));
        }
    }

    @Nested
    @DisplayName("Test constructors")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ConstructorTest extends AbstractReflTest {

        private Object[] getConstructors() {
            return new Object[]{
                    (Supplier<Refl<TestClass>>) () -> new Refl<>(TestClass.class.getCanonicalName(), "James"),
                    (Supplier<Refl<TestClass>>) () -> new Refl<>(TestClass.class.getCanonicalName(), new Class[]{String.class}, "James"),
                    (Supplier<Refl<TestClass>>) () -> new Refl<>(TestClass.class, "James"),
                    (Supplier<Refl<TestClass>>) () -> new Refl<>(TestClass.class, new Class[]{String.class}, "James")
            };
        }

        @ParameterizedTest
        @MethodSource("getConstructors")
        void test(Supplier<Refl<TestClass>> supplier) {
            assertEquals(this.refl, supplier.get());
            assertEquals(this.refl.getObject(), supplier.get().getObject());
        }

        @Test
        void testClass() {
            assertEquals(TestClass.class, new Refl<>(TestClass.class).getObject());
        }
    }
}