package io.taptalk.taptalklive.Exception;

public class TTLApiRefreshTokenRunningException extends Exception {

    public TTLApiRefreshTokenRunningException() {
        super("Refresh Token is Running");
    }
}
