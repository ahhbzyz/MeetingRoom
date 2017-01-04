package com.badoo.meetingroom.data.exception;

/**
 * Created by zhangyaozhong on 21/12/2016.
 */

public class NetworkConnectionException extends Exception {
    public NetworkConnectionException() {
        super("Please check network connection");
    }

    public NetworkConnectionException(String message) {
        super(message);
    }
}
