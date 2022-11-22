package com.coditory.quark.i18n.loader;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

public record MatchedPath(
        String template,
        Pattern filePattern,
        String baseDirectory,
        File file,
        Locale locale,
        String prefix
) {
}
