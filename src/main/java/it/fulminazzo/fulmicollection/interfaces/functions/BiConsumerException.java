package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a two parameters consumer that throws an exception.
 * (F, S) -&#62; void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface BiConsumerException<F, S, X extends Throwable> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @throws X the exception
     */
    void accept(F first, S second) throws X;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default BiConsumerException<F, S, X> andThen(BiConsumerException<? super F, ? super S, X> after) {
        return (f, s) -> {
            this.accept(f, s);
            after.accept(f, s);
        };
    }
}
