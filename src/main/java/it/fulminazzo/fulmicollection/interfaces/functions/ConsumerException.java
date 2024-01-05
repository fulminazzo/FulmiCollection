package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a one parameter consumer that throws an exception.
 * (T) -&#62; void
 *
 * @param <T> the type parameter
 */
@FunctionalInterface
public interface ConsumerException<T> {

    /**
     * Accept function.
     *
     * @param element the element
     * @throws Exception the exception
     */
    void accept(T element) throws Exception;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default ConsumerException<T> andThen(ConsumerException<? super T> after) {
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
