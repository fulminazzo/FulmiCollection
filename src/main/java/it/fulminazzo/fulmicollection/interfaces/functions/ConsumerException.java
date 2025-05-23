package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a one parameter consumer that throws an exception.
 * (T) -&#62; void
 *
 * @param <T> the type parameter
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface ConsumerException<T, X extends Throwable> {

    /**
     * Accept function.
     *
     * @param element the element
     * @throws X the exception
     */
    void accept(T element) throws X;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default ConsumerException<T, X> andThen(ConsumerException<? super T, X> after) {
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
