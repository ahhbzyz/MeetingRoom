package com.badoo.meetingroom.data.exception;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GooglePlayServicesAvailabilityException extends Exception {

    private final int connectionStatusCode;

    public GooglePlayServicesAvailabilityException(final int connectionStatusCode) {
        this.connectionStatusCode = connectionStatusCode;
    }

    public int getConnectionStatusCode() {
        return connectionStatusCode;
    }

}
