package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * A collection of utilities for exceptions.
 */
public class ExceptionUtils {

    /**
     * Converts a {@link RuntimeException}, {@link InvocationTargetException} or {@link ExecutionException} to its cause (if not null).
     *
     * @param exception the exception
     * @return the exception
     */
    public static @NotNull Throwable unwrapRuntimeException(@NotNull Throwable exception) {
        while (exception.getCause() != null &&
                (exception.getClass().equals(RuntimeException.class) ||
                        (exception.getClass().equals(InvocationTargetException.class) ||
                        exception.getClass().equals(ExecutionException.class))))
            exception = exception.getCause();
        return exception;
    }

    /**
     * Throw an exception.
     * If the exception is of type {@link RuntimeException}, throw itself.
     *
     * @param throwable the throwable
     */
    public static void throwException(@NotNull Throwable throwable) {
        throwable = unwrapRuntimeException(throwable);
        if (throwable instanceof RuntimeException) throw (RuntimeException) throwable;
        else throw new RuntimeException(throwable);
    }
}
