package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a five parameters consumer.
 * (F, S, T, Q, P) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 * @param <P> the type parameter
 */
@FunctionalInterface
public interface PentaConsumer<F, S, T, Q, P> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     * @param fifth  the fifth argument
     */
    void accept(F first, S second, T third, Q fourth, P fifth);

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default PentaConsumer<F, S, T, Q, P> andThen(PentaConsumer<? super F, ? super S, ? super T, ? super Q, ? super P> after) {
        return (f, s, t, q, p) -> {
            this.accept(f, s, t, q, p);
            after.accept(f, s, t, q, p);
        };
    }
}
