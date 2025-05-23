package it.fulminazzo.fulmicollection.interfaces.functions;

/**
 * Interface that represents a supplier that throws an exception.
 * () -&#62; R
 *
 * @param <R> the return type
 * @param <X> the type of the exception
 */
@FunctionalInterface
public interface SupplierException<R, X extends Throwable> {

    /**
     * Gets the result.
     *
     * @return the returning object
     * @throws X the exception
     */
    R get() throws X;

}
