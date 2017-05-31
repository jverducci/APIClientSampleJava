package com.tradeshift.exception;

/**
 * This is a runtime exception that is thrown when an API
 * call to a Tradeshift endpoint fails for any reason.
 */
public class TradeshiftRestClientException extends RuntimeException {

    public TradeshiftRestClientException(String message) {
        super(message);
    }
}
