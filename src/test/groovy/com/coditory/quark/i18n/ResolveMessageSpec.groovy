package com.coditory.quark.i18n

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.DE
import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.EN_GB
import static com.coditory.quark.i18n.Locales.EN_US
import static com.coditory.quark.i18n.Locales.PL
import static com.coditory.quark.i18n.Locales.PL_PL

class ResolveMessageSpec extends Specification {
    @Unroll
    def "should resolve message for locale: #locale"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "hello", "Cześć")
                    .addMessage(EN, "hello", "Hello")
                    .addMessage(EN_US, "hello", "Hello - US")
                    .addMessage(EN_GB, "hello", "Hello - GB")
                    .build()
                    .localize(locale)
        when:
            String result = messages.getMessage("hello")
        then:
            result == expected

        where:
            locale                         | expected
            PL                             | "Cześć"
            PL_PL                          | "Cześć"
            DE                             | "Hello"
            EN                             | "Hello"
            EN_US                          | "Hello - US"
            EN_GB                          | "Hello - GB"
            Locale.forLanguageTag("en-XX") | "Hello"
    }

    def "should throw error for missing translation"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.bye", "Bye")
                    .build()
                    .localize(PL)
        when:
            messages.getMessage("home.xxx")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Missing message pl:home.xxx"

        when:
            messages.getMessage("home.xxx", 123, "xyz")
        then:
            e = thrown(I18nMessagesException)
            e.message == "Missing message pl:home.xxx(123, xyz)"
    }

    def "should throw error for missing translation and no default locale"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.bye", "Bye")
                    .build()
                    .localize(PL)
        when:
            messages.getMessage("home.bye")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Missing message pl:home.bye"

        when:
            messages.getMessage("home.bye", 123, "xyz")
        then:
            e = thrown(I18nMessagesException)
            e.message == "Missing message pl:home.bye(123, xyz)"
    }

    def "should return message with two string arguments"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "hello", "Witaj {0} {1}")
                    .addMessage(EN, "bye", "Bye {0} {1}")
                    .build()
                    .localize(PL)
        when:
            String result = messages.getMessage("hello", "Jan", "Kowalski")
        then:
            result == "Witaj Jan Kowalski"

        when:
            result = messages.getMessage("bye", "John", "Doe")
        then:
            result == "Bye John Doe"
    }
}
