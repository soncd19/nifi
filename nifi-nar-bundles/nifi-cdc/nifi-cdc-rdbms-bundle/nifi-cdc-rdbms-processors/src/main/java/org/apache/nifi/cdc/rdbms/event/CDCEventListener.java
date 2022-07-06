package org.apache.nifi.cdc.rdbms.event;

/**
 * Created by SonCD on 4/16/2020
 */
public interface CDCEventListener<T> {
    void onEvent(T event) ;
}
