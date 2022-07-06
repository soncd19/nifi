package org.apache.nifi.cdc.rdbms.exception;

/**
 * Created by SonCD on 4/14/2020
 */
public class NifiCDCValidationException extends RuntimeException {

    public NifiCDCValidationException(String message) {
        super(message);
    }
}
