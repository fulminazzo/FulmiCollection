package it.fulminazzo.fulmicollection.utils;

import it.fulminazzo.fulmicollection.exceptions.*;
import it.fulminazzo.fulmicollection.interfaces.functions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassUtilsTest {

    @Test
    void findClassInExceptions() {
        assertEquals(ClassCannotBeNullException.class, ClassUtils.findClassInPackages(ClassCannotBeNullException.class.getPackage().getName(),
                ClassCannotBeNullException.class.getSimpleName()));
    }

    @Test
    void findClassesInExceptions() {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(ClassCannotBeNullException.class.getPackage().getName());
        Set<Class<?>> expected = new LinkedHashSet<>(Arrays.asList(ClassCannotBeNullException.class, ClassCannotBeNullExceptionTest.class,
                GeneralCannotBeNullException.class, NameCannotBeNullException.class, NameCannotBeNullExceptionTest.class));
        assertEquals(expected, classes);
    }

    @Test
    void findClassesInExceptionsDotted() {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(ClassCannotBeNullException.class.getPackage().getName() + ".");
        Set<Class<?>> expected = new LinkedHashSet<>(Arrays.asList(ClassCannotBeNullException.class, ClassCannotBeNullExceptionTest.class,
                GeneralCannotBeNullException.class, NameCannotBeNullException.class, NameCannotBeNullExceptionTest.class));
        assertEquals(expected, classes);
    }

    @Test
    void findClassesInInterfacesFunctionsRecursive() {
        String packageName = BiConsumerException.class.getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(packageName);
        Set<Class<?>> expected = new HashSet<>(Arrays.asList(
                BiConsumerException.class, BiFunctionException.class, ConsumerException.class, FunctionException.class,
                PentaConsumer.class, PentaConsumerException.class, PentaFunction.class, PentaFunctionException.class,
                TetraConsumer.class, TetraConsumerException.class, TetraFunction.class, TetraFunctionException.class,
                TriConsumer.class, TriConsumerException.class, TriFunction.class, TriFunctionException.class
        ));
        assertEquals(expected, classes);
    }

    @Test
    void findClassesEmptyString() {
        assertEquals(new HashSet<>(), ClassUtils.findClassesInPackage(""));
    }

    @Test
    void findClassesInNonExistingPackage() {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage("super.cool.package.name");
        assertEquals(new LinkedHashSet<>(), classes);
    }

    @Test
    void findClassesInNull() {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(null);
        assertEquals(new LinkedHashSet<>(), classes);
    }

    @Test
    void findClassesInJar() {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(Assertions.class.getPackage().getName() + ".io");
        assertEquals(new LinkedHashSet<>(Arrays.asList(CleanupMode.class, TempDir.class)), classes);
    }
}