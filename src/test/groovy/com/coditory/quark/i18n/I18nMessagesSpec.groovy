package com.coditory.quark.i18n

import spock.lang.Specification

import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.PL

class I18nMessagesSpec extends Specification {
    def "should resolve message"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "hello", "Cześć")
                    .addMessage(EN, "hello", "Hello")
                    .build()
                    .localized(PL)
        when:
            String result = messages.getMessage("hello")
        then:
            result == "Cześć"
    }

    def "should resolve message from default locale"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(EN, "bye", "Bye")
                    .build()
                    .localized(PL)
        when:
            String result = messages.getMessage("bye")
        then:
            result == "Bye"
    }

    def "should throw error for missing translation"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .addMessage(PL, "home.hello", "Witamy")
                    .addMessage(EN, "home.bye", "Bye")
                    .build()
                    .localized(PL)
        when:
            messages.getMessage("home.helloo")
        then:
            I18nMessagesException e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message pl:home.helloo"

        when:
            messages.getMessage("home.bye", 123, "xyz")
        then:
            e = thrown(I18nMessagesException)
            e.message == "Could not resolve i18n message pl:home.bye(123, xyz)"
    }

    def "should return message with two string arguments"() {
        given:
            I18nMessages messages = I18nMessagePack.builder()
                    .setDefaultLocale(EN)
                    .addMessage(PL, "hello", "Witaj {0} {1}")
                    .addMessage(EN, "bye", "Bye {0} {1}")
                    .build()
                    .localized(PL)
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
