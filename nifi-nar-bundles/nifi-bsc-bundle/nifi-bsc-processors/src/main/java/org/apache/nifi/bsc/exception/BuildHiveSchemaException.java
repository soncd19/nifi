package org.apache.nifi.bsc.exception;

/**
 * Created by SonCD on 4/20/2020
 */
public class BuildHiveSchemaException extends RuntimeException {

    public BuildHiveSchemaException(String message){
        super(message);
    }

    public BuildHiveSchemaException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
