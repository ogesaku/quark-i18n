package com.coditory.quark.i18n;

import java.util.Locale;

interface MessageTemplateFormatter {
    String format(Locale locale, String message, Object... args);
}
