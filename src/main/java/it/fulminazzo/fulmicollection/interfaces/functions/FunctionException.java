package it.fulminazzo.fulmicollection.interfaces.functions;

import java.util.function.Function;

/**
 * Interface that represents a one parameter function that throws an exception.
 * (F) -&#62; R
 *
 * @param <F> the type parameter
 * @param <R> the return type
 */
@FunctionalInterface
public interface FunctionException<F, R> {

    /**
     * Apply function.
     *
     * @param first the first argument
     * @return the returning object
     * @throws Exception the exception
     */
    R apply(F first) throws Exception;

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a TriFunction that combines this and the function.
     */
    default <V> FunctionException<F, V> andThen(Function<? super R, ? extends V> after) {
        return (t) -> after.apply(this.apply(t));
    }
}
