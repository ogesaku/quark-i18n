package com.coditory.quark.i18n.formats

import com.coditory.quark.i18n.I18nMessagePack
import com.coditory.quark.i18n.I18nMessages
import spock.lang.Specification

import static com.coditory.quark.i18n.Locales.EN_US
import static com.coditory.quark.i18n.Locales.PL_PL

class IcuPluralFormatSpec extends Specification {
    def "should format a simple message with plural format"() {
        given:
            String key = "your-items"
            I18nMessages messages = I18nMessagePack.builder()
                    .addMessage(EN_US, key, """
                    You have {0, plural,
                        =0 {no messages}
                        one {1 message}
                        other {# messages}
                    }.
                    """.stripIndent().trim())
                    .addMessage(PL_PL, key, """
                    {0, plural,
                        =0 {Nie masz wiadomości}
                        one {Masz 1 wiadomość}
                        other {Masz # wiadomości}
                    }.
                    """.stripIndent().trim())
                    .build()
                    .localize(locale)
        when:
            String result = messages.getMessage(key, count)
        then:
            result == expected
        where:
            locale | count || expected
            EN_US  | 0     || "You have no messages."
            EN_US  | 1     || "You have 1 message."
            EN_US  | 2     || "You have 2 messages."
            EN_US  | 100   || "You have 100 messages."
            PL_PL  | 0     || "Nie masz wiadomości."
            PL_PL  | 1     || "Masz 1 wiadomość."
            PL_PL  | 2     || "Masz 2 wiadomości."
            PL_PL  | 100   || "Masz 100 wiadomości."
    }

    def "should format a complex message with plural format"() {
        given:
            String key = "your-items"
            I18nMessages messages = I18nMessagePack.builder()
                    .addMessage(EN_US, key, """
                    {name} has sent you {count, plural,
                      =0 {no messages}
                      =1 {a message}
                      other {{count, number, integer} messages}
                    }.
                    """.stripIndent().trim())
                    .addMessage(PL_PL, key, """
                    {name}{count, plural, =0 { nie} other {}} {gender, select, male {wysłał} female {wysłała} other {wysłało}} ci {count, plural,
                        =0 {żadnych wiadomości}
                        =1 {wiadomość}
                        other {{count, number, integer} wiadomości}
                      }.
                    """.stripIndent().trim())
                    .build()
                    .localize(locale)
        when:
            String result = messages.getMessage(key, [name: name, gender: gender, count: count])
        then:
            result == expected
        where:
            locale | name    | gender   | count || expected
            EN_US  | "Alice" | "female" | 0     || "Alice has sent you no messages."
            EN_US  | "Bob"   | "male"   | 1     || "Bob has sent you a message."
            EN_US  | "Alice" | "female" | 5     || "Alice has sent you 5 messages."
            PL_PL  | "Alice" | "female" | 0     || "Alice nie wysłała ci żadnych wiadomości."
            PL_PL  | "Bob"   | "male"   | 1     || "Bob wysłał ci wiadomość."
            PL_PL  | "Alice" | "female" | 5     || "Alice wysłała ci 5 wiadomości."
    }
}
