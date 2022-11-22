package com.coditory.quark.i18n;

public class I18nMessagesException extends RuntimeException {
    public I18nMessagesException(String message, Throwable cause) {
        super(message, cause);
    }

    public I18nMessagesException(String message) {
        super(message);
    }
}
