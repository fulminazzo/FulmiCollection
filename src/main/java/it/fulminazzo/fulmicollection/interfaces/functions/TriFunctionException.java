package it.fulminazzo.fulmicollection.interfaces.functions;

import java.util.function.Function;

/**
 * Interface that represents a three parameters function that throws an exception.
 * (F, S, T) -&#62; R
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <R> the return type
 */
@FunctionalInterface
public interface TriFunctionException<F, S, T, R> {

    /**
     * Apply function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @return the returning object
     * @throws Exception the exception
     */
    R apply(F first, S second, T third) throws Exception;

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a TriFunction that combines this and the function.
     */
    default <V> TriFunctionException<F, S, T, V> andThen(Function<? super R, ? extends V> after) {
        return (f, s, t) -> after.apply(this.apply(f, s, t));
    }
}
