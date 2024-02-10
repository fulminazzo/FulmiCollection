package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ReflObjectTest {

    private static Object[][] getConstructorParameters() {
        return new Object[][]{
                new Object[]{TestClass.class.getCanonicalName(), new Object[]{"test"}, null},
                new Object[]{TestClass.class.getCanonicalName(), new Class[]{String.class}, new Object[]{"test"}},
                new Object[]{TestClass.class, new Object[]{"test"}, null},
                new Object[]{TestClass.class, new Class[]{String.class}, new Object[]{"test"}},
                new Object[]{new TestClass("test"), TestClass.class, null},
                new Object[]{new TestClass("test"), null, null}
        };
    }

    @ParameterizedTest
    @MethodSource("getConstructorParameters")
    void testConstructorParameters(Object param1, Object param2, Object param3) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object[] parameters = Stream.of(param1, param2, param3).filter(Objects::nonNull).toArray(Object[]::new);
        Class<?>[] paramTypes = ReflectionUtils.objectsToClasses(parameters);
        Constructor<?> constructor = ReflectionUtils.getConstructor(ReflObject.class, paramTypes);
        assertNotNull(constructor);
        ReflObject<?> reflObject = (ReflObject<?>) constructor.newInstance(parameters);
        assertEquals(TestClass.class, reflObject.objectClass);
        assertInstanceOf(TestClass.class, reflObject.object);
        assertEquals("test", ((TestClass) reflObject.object).name);
    }

    static class TestClass {
        public String name;

        public TestClass(String name) {
            this.name = name;
        }

        public String printName() {
            return name;
        }
    }
}