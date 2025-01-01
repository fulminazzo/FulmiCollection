package it.fulminazzo.fulmicollection.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

public class TestUtils {

    public static <C extends Collection<Method>> C computeNonObjectMethods(final Callable<C> methodSupplier) {
        try {
            C methods = methodSupplier.call();
            methods.removeIf(m -> Arrays.asList(Object.class.getDeclaredMethods()).contains(m));
            return methods;
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            else throw new RuntimeException(e);
        }
    }

}
