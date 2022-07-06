package org.apache.nifi.cdc.rdbms.exception;

/**
 * Created by SonCD on 4/14/2020
 */
public class NifiCreationException extends RuntimeException {

    public NifiCreationException(String message) {
        super(message);
    }

    public NifiCreationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
