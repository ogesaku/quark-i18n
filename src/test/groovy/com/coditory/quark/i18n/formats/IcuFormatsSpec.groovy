package com.coditory.quark.i18n.formats

import com.coditory.quark.i18n.I18nMessagePack
import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.PL

class IcuFormatsSpec extends Specification {
    @Unroll
    def "should format message with plural format and numbers"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "msg", "Numer jest {0, plural, =0 {zerem} =0.75 {równy trzy czwarte} other {równy #}}")
                    .addMessage(EN, "msg", "The number is {0, plural, =0 {zero} =1 {one} other {equal to #}}")
                    .build()
        when:
            String result = messages.getMessage(lang, "msg", number)
        then:
            result == expected

        where:
            lang | number | expected
            PL   | -100   | "Numer jest równy -100"
            PL   | 100    | "Numer jest równy 100"
            PL   | 0      | "Numer jest zerem"
            PL   | 0.5    | "Numer jest równy 0,5"
            PL   | 0.75   | "Numer jest równy trzy czwarte"
            PL   | 1      | "Numer jest równy 1"
            EN   | -100   | "The number is equal to -100"
            EN   | 100    | "The number is equal to 100"
            EN   | 0      | "The number is zero"
            EN   | 1      | "The number is one"
            EN   | 0.5    | "The number is equal to 0.5"
    }

    @Unroll
    def "should format message with plural format and numbers"() {
        given:
            I18nMessagePack messages = I18nMessagePack.builder()
                    .addMessage(PL, "msg", "Numer jest {0, plural, =0 {zerem} =0.75 {równy trzy czwarte} other {równy #}}")
                    .addMessage(EN, "msg", "The number is {0, plural, =0 {zero} =1 {one} other {equal to #}}")
                    .build()
        when:
            String result = messages.getMessage(lang, "msg", number)
        then:
            result == expected

        where:
            lang | number | expected
            PL   | -100   | "Numer jest równy -100"
            PL   | 100    | "Numer jest równy 100"
            PL   | 0      | "Numer jest zerem"
            PL   | 0.5    | "Numer jest równy 0,5"
            PL   | 0.75   | "Numer jest równy trzy czwarte"
            PL   | 1      | "Numer jest równy 1"
            EN   | -100   | "The number is equal to -100"
            EN   | 100    | "The number is equal to 100"
            EN   | 0      | "The number is zero"
            EN   | 1      | "The number is one"
            EN   | 0.5    | "The number is equal to 0.5"
    }
}
