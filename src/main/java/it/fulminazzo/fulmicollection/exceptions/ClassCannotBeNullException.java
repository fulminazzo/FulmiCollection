package it.fulminazzo.fulmicollection.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Class cannot be null exception.
 */
public class ClassCannotBeNullException extends GeneralCannotBeNullException {

    public ClassCannotBeNullException(@NotNull String variableName) {
        super("Class for " + variableName);
    }
}
