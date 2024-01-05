package it.fulminazzo.fulmicollection.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Name cannot be null exception.
 */
public class NameCannotBeNullException extends GeneralCannotBeNullException {

    public NameCannotBeNullException(@NotNull String variableName) {
        super("Name for " + variableName);
    }
}
