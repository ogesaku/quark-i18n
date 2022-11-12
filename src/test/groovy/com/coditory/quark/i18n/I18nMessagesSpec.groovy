package com.coditory.quark.i18n

import com.coditory.quark.i18n.api.I18nMessagePack
import com.coditory.quark.i18n.api.I18nMessagesException
import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.PL

class I18nMessagesSpec extends Specification {
    @Unroll
    def "should resolve message for #locale"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
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
    }

    def "should throw error for missing translation"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.hello", "Hello")
                    .build()
        when:
            messages.getMessage(PL, "home.helloo")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message (locale: pl, key: home.helloo, args: [])"

        when:
            messages.getMessage(Locale.GERMAN, "home.hello")
        then:
            e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message (locale: de, key: home.hello, args: [])"
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

    @Unroll
    def "should format message with plurals"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "msg", "Numer jest {0 | plural ujemny 0 zerem 1 'równy jeden' 1 'większy niż jeden'}")
                    .addMessage(EN, "msg", "The number is {0 | plural negative 0 zero 1 one 1 'more than 1'}")
                    .build()
        when:
            String result = messages.getMessage(lang, "msg", number)
        then:
            result == expected

        where:
            lang | number | expected
            PL   | -100   | "Numer jest ujemny"
            PL   | -1     | "Numer jest ujemny"
            PL   | 0      | "Numer jest zerem"
            PL   | 0.5    | "Numer jest zerem"
            PL   | 1      | "Numer jest równy jeden"
            PL   | 2      | "Numer jest większy niż jeden"
            EN   | -100   | "The number is negative"
            EN   | -1     | "The number is negative"
            EN   | 0      | "The number is zero"
            EN   | 0.5    | "The number is zero"
            EN   | 1      | "The number is one"
            EN   | 2      | "The number is more than 1"
    }
}
