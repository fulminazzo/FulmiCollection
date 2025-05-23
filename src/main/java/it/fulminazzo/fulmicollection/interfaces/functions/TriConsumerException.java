package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a three parameters consumer that throws an exception.
 * (F, S, T) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface TriConsumerException<F, S, T, X extends Throwable> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @throws X the exception
     */
    void accept(F first, S second, T third) throws X;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default TriConsumerException<F, S, T, X> andThen(TriConsumerException<? super F, ? super S, ? super T, X> after) {
        return (f, s, t) -> {
            this.accept(f, s, t);
            after.accept(f, s, t);
        };
    }
}
