package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a four parameters consumer that throws an exception.
 * (F, S, T, Q) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface TetraConsumerException<F, S, T, Q, X extends Throwable> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     * @throws X the exception
     */
    void accept(F first, S second, T third, Q fourth) throws X;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default TetraConsumerException<F, S, T, Q, X> andThen(TetraConsumerException<? super F, ? super S, ? super T, ? super Q, X> after) {
        return (f, s, t, q) -> {
            this.accept(f, s, t, q);
            after.accept(f, s, t, q);
        };
    }
}
