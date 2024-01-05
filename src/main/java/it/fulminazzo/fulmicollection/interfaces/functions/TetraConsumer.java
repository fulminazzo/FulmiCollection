package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a four parameters consumer.
 * (F, S, T, Q) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 */
@FunctionalInterface
public interface TetraConsumer<F, S, T, Q> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     */
    void accept(F first, S second, T third, Q fourth);

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default TetraConsumer<F, S, T, Q> andThen(TetraConsumer<? super F, ? super S, ? super T, ? super Q> after) {
        return (f, s, t, q) -> {
            this.accept(f, s, t, q);
            after.accept(f, s, t, q);
        };
    }
}
