package com.coditory.quark.i18n;

import java.util.Locale;
import java.util.TimeZone;

public class I18nSystemDefaults {
    private I18nSystemDefaults() {
        throw new RuntimeException("Utility class constructor");
    }

    public static void setupNormalized() {
        System.setProperty("user.timezone", "GMT");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        System.setProperty("user.language", "en");
        System.setProperty("user.region", "US");
        System.setProperty("user.variant", "");
        Locale.setDefault(Locale.US);
    }
}
