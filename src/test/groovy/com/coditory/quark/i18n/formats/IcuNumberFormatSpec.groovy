package com.coditory.quark.i18n.formats

import com.coditory.quark.i18n.I18nMessagePack
import com.coditory.quark.i18n.I18nMessagePackFactory
import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.Locales.EN_US
import static com.coditory.quark.i18n.Locales.PL_PL

class IcuNumberFormatSpec extends Specification {
    static final I18nMessagePack messages = I18nMessagePackFactory.emptyMessagePack()

    @Unroll
    def "should format message with number format: #format #value"() {
        when:
            String polishMessage = messages.format(PL_PL, format, value)
            String englishMessage = messages.format(EN_US, format, value)
        then:
            polishMessage == expectedPl
            englishMessage == expectedEn
        where:
            format                      | value                      | expectedEn    | expectedPl
            "{0}"                       | 1024.5                     | "1,024.5"     | "1 024,5"
            "{0}"                       | -1024.5                    | "-1,024.5"    | "-1 024,5"
            "{0, number}"               | 1024.5                     | "1,024.5"     | "1 024,5"
            "{0, number, integer}"      | 1024.5                     | "1,024"       | "1 024"
            "{0, number, currency}"     | 1024.5                     | "\$1,024.50"  | "1 024,50 zł"
            "{0, number, currency}"     | -1024.5                    | "-\$1,024.50" | "-1 024,50 zł"
            "{0, number, currency}"     | new BigDecimal("432.6542") | "\$432.65"    | "432,65 zł"
            "{0, number, percent}"      | 0.5                        | "50%"         | "50%"
            "{0, number, percent}"      | 1.5                        | "150%"        | "150%"
            "{0, number, percent}"      | 5                          | "500%"        | "500%"
            "{0, number, percent}"      | -1.5                       | "-150%"       | "-150%"
            "{0, number,00000.0000}"    | -123.45                    | "-00123.4500" | "-00123,4500"
            "{0, number,::percent .00}" | 0.25                       | "0.25%"       | "0,25%"
    }
}
