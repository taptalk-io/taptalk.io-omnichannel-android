package io.taptalk.taptalklive.Exception;

public class TAPApiRefreshTokenRunningException extends Exception {

    public TAPApiRefreshTokenRunningException() {
        super("Refresh Token is Running");
    }
}
