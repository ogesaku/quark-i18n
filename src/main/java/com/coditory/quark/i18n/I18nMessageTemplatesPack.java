package com.coditory.quark.i18n;

import com.coditory.quark.i18n.api.I18nKey;
import com.coditory.quark.i18n.api.I18nKeyGenerator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

class I18nMessageTemplatesPack {
    private final Map<I18nKey, String> messages;
    private final I18nKeyGenerator keyGenerator;

    public I18nMessageTemplatesPack(Map<I18nKey, String> messages, I18nKeyGenerator keyGenerator) {
        this.messages = messages;
        this.keyGenerator = keyGenerator;
    }

    public Map<I18nKey, String> filterMessagesWith(Locale locale) {
        return messages.entrySet().stream()
                .filter(e -> e.getKey().locale().equals(locale))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public I18nMessageTemplates withLocale(Locale locale) {
        return new I18nMessageTemplates(messages, keyGenerator, locale);
    }

    public Set<Locale> getLocales() {
        return messages.keySet()
                .stream()
                .map(I18nKey::locale)
                .collect(toSet());
    }

    public Stream<Map.Entry<I18nKey, String>> entries() {
        return messages.entrySet().stream();
    }

    static public I18nRawMessagesBuilder builder() {
        return new I18nRawMessagesBuilder();
    }

    static class I18nRawMessagesBuilder {
        private final Map<I18nKey, String> messages = new HashMap<>();
        private I18nKeyGenerator keyGenerator;

        public I18nRawMessagesBuilder addMessage(Locale locale, String key, String message) {
            this.messages.put(I18nKey.of(locale, key), message);
            return this;
        }

        public I18nRawMessagesBuilder withI18nKeyGenerator(I18nKeyGenerator keyGenerator) {
            this.keyGenerator = requireNonNull(keyGenerator);
            return this;
        }

        public I18nMessageTemplatesPack build() {
            return new I18nMessageTemplatesPack(messages, keyGenerator);
        }
    }
}
