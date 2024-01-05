package it.fulminazzo.fulmicollection.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * General cannot be null exception.
 */
public class GeneralCannotBeNullException extends RuntimeException {

    public GeneralCannotBeNullException(@NotNull String objectName) {
        super("%object% cannot be null.".replace("%object%", objectName));
    }
}
