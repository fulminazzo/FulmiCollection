package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a one parameter function that throws an exception.
 * (F) -&#62; R
 *
 * @param <F> the type parameter
 * @param <R> the return type
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface FunctionException<F, R, X extends Throwable> {

    /**
     * Apply function.
     *
     * @param first the first argument
     * @return the returning object
     * @throws X the exception
     */
    R apply(F first) throws X;

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a TriFunction that combines this and the function.
     */
    default <V> FunctionException<F, V, X> andThen(FunctionException<? super R, ? extends V, X> after) {
        return (t) -> after.apply(this.apply(t));
    }
}
