package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a three parameters consumer.
 * (F, S, T) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 */
@FunctionalInterface
public interface TriConsumer<F, S, T> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     */
    void accept(F first, S second, T third);

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default TriConsumer<F, S, T> andThen(TriConsumer<? super F, ? super S, ? super T> after) {
        return (f, s, t) -> {
            this.accept(f, s, t);
            after.accept(f, s, t);
        };
    }
}
