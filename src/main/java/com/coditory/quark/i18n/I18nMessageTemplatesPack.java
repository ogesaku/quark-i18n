package com.coditory.quark.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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

    Map<I18nKey, String> filterMessagesWith(Locale locale) {
        expectNonNull(locale, "locale");
        return messages.entrySet().stream()
                .filter(e -> e.getKey().locale().equals(locale))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
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

    Stream<Map.Entry<I18nKey, String>> entries() {
        return messages.entrySet().stream();
    }

    static I18nRawMessagesBuilder builder() {
        return new I18nRawMessagesBuilder();
    }

    static final class I18nRawMessagesBuilder {
        private final Map<I18nKey, String> messages = new HashMap<>();
        private I18nKeyGenerator keyGenerator;

        public I18nRawMessagesBuilder addMessage(Locale locale, String key, String message) {
            expectNonNull(locale, "locale");
            expectNonNull(key, "key");
            expectNonNull(message, "message");
            this.messages.put(I18nKey.of(locale, key), message);
            return this;
        }

        public I18nRawMessagesBuilder withI18nKeyGenerator(I18nKeyGenerator keyGenerator) {
            this.keyGenerator = expectNonNull(keyGenerator, "keyGenerator");
            return this;
        }

        public I18nMessageTemplatesPack build() {
            return new I18nMessageTemplatesPack(messages, keyGenerator);
        }
    }
}
