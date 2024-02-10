package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Test fields")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FieldTest extends AbstractReflTest {

        private Object[] getField() {
            return new Object[]{
                    (Supplier<Field>) () -> this.refl.getFieldNameless(String.class.getCanonicalName()),
                    (Supplier<Field>) () -> this.refl.getField(String.class),
                    (Supplier<Field>) () -> this.refl.getField("name"),
            };
        }

        private Object[] getFieldObject() {
            return new Object[]{
                    (Supplier<String>) () -> this.refl.getFieldObjectNameless(String.class.getCanonicalName()),
                    (Supplier<String>) () -> this.refl.getFieldObject(String.class),
                    (Supplier<String>) () -> this.refl.getFieldObject("name"),
                    (Supplier<String>) () -> {
                        try {
                            return this.refl.getFieldObject(TestClass.class.getDeclaredField("name"));
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

        @Test
        void testGetFieldFromNull() {
            assertThrows(IllegalStateException.class, () -> new Refl<>(null).getField("test"));
        }

        @Test
        void testGetFieldObjectFromNull() {
            assertThrows(IllegalStateException.class, () -> new Refl<>(null).getFieldObject("test"));
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