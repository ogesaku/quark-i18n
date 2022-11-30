package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;

import java.io.File;
import java.util.Map;

@FunctionalInterface
public interface I18nKeyParser {
    Map<I18nKey, String> parseKeys(I18nPathPattern pathPattern, File file, Map<String, Object> parsed);
}
