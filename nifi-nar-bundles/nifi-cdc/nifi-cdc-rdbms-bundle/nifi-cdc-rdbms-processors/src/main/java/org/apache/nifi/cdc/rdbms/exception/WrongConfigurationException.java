package org.apache.nifi.cdc.rdbms.exception;

/**
 * Created by SonCD on 4/14/2020
 */
public class WrongConfigurationException extends Exception {
    public WrongConfigurationException(String message) {
        super(message);
    }
}
