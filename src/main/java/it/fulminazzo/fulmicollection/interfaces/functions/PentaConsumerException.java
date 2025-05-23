package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a five parameters consumer that throws an exception.
 * (F, S, T, Q, P) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 * @param <P> the type parameter
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface PentaConsumerException<F, S, T, Q, P, X extends Throwable> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     * @param fifth  the fifth argument
     * @throws X the exception
     */
    void accept(F first, S second, T third, Q fourth, P fifth) throws X;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default PentaConsumerException<F, S, T, Q, P, X> andThen(PentaConsumerException<? super F, ? super S, ? super T, ? super Q, ? super P, X> after) {
        return (f, s, t, q, p) -> {
            this.accept(f, s, t, q, p);
            after.accept(f, s, t, q, p);
        };
    }
}
