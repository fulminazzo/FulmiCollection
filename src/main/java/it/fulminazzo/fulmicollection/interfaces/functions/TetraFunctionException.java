package it.fulminazzo.fulmicollection.interfaces.functions;

import java.util.function.Function;

/**
 * Interface that represents a four parameters function that throws an exception.
 * (F, S, T, Q) -&#62; R
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 * @param <R> the return type
 */
@FunctionalInterface
public interface TetraFunctionException<F, S, T, Q, R> {

    /**
     * Apply function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     * @return the returning object
     * @throws Exception the exception
     */
    R apply(F first, S second, T third, Q fourth) throws Exception;

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a QuadFunction that combines this and the function.
     */
    default <V> TetraFunctionException<F, S, T, Q, V> andThen(Function<? super R, ? extends V> after) {
        return (f, s, t, q) -> after.apply(this.apply(f, s, t, q));
    }
}
