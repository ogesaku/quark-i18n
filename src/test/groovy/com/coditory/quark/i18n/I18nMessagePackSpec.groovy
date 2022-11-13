package com.coditory.quark.i18n

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.DE
import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.PL

class I18nMessagePackSpec extends Specification {
    @Unroll
    def "should resolve message for #locale"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "hello", "Cześć")
                    .addMessage(EN, "hello", "Hello")
                    .build()
        when:
            String result = messages.getMessage(locale, "hello")
        then:
            result == expected
        where:
            locale | expected
            PL     | "Cześć"
            EN     | "Hello"
            DE     | "Hello"
    }

    def "should throw error for missing translation"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.hello", "Hello")
                    .build()
        when:
            messages.getMessage(PL, "home.helloo")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message pl:home.helloo"
    }

    def "should throw error for missing translation when there is no default locale"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.hello", "Hello")
                    .build()
        when:
            messages.getMessage(PL, "home.helloo")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message pl:home.helloo"

        when:
            messages.getMessage(DE, "home.hello", 123, "xyz")
        then:
            e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message de:home.hello(123, xyz)"
    }

    def "should return message with two string arguments"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "hello", "Witaj {0} {1}")
                    .addMessage(EN, "hello", "Hello {0} {1}")
                    .build()
        when:
            String result = messages.getMessage(PL, "hello", "Jan", "Kowalski")
        then:
            result == "Witaj Jan Kowalski"

        when:
            result = messages.getMessage(EN, "hello", "John", "Doe")
        then:
            result == "Hello John Doe"
    }
}
