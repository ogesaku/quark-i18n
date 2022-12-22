package com.coditory.quark.i18n;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

final class I18nMessageTemplatesPack {
    private final Map<I18nKey, String> messages;
    private final I18nKeyGenerator keyGenerator;

    I18nMessageTemplatesPack(Map<I18nKey, String> messages, I18nKeyGenerator keyGenerator) {
        expectNonNull(messages, "messages");
        this.messages = Map.copyOf(messages);
        this.keyGenerator = expectNonNull(keyGenerator, "keyGenerator");
    }

    Set<Map.Entry<I18nKey, String>> entries() {
        return messages.entrySet();
    }

    Set<I18nKey> getKeys() {
        return messages.keySet();
    }

    Collection<String> getValues() {
        return messages.values();
    }

    I18nMessageTemplates withLocale(Locale locale) {
        expectNonNull(locale, "locale");
        return new I18nMessageTemplates(messages, keyGenerator, locale);
    }

    Set<Locale> getLocales() {
        return messages.keySet()
                .stream()
                .map(I18nKey::locale)
                .collect(toSet());
    }
}
