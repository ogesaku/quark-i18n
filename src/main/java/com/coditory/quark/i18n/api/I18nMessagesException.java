package com.coditory.quark.i18n.api;

public class I18nMessagesException extends RuntimeException {
    public I18nMessagesException(String message) {
        super(message);
    }

    public I18nMessagesException(String message, Throwable cause) {
        super(message, cause);
    }
}
