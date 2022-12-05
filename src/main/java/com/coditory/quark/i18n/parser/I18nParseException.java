package com.coditory.quark.i18n.parser;

import com.coditory.quark.i18n.I18nMessagesException;

public class I18nParseException extends I18nMessagesException {
    public I18nParseException(String message) {
        super(message);
    }

    public I18nParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
