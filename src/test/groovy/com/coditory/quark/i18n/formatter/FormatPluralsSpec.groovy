package com.coditory.quark.i18n.formatter

import com.coditory.quark.i18n.I18nMessagePack
import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.PL

class FormatPluralsSpec extends Specification {
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
