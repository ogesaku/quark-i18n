package com.coditory.quark.i18n

import com.coditory.quark.i18n.base.InMemI18nLoader
import spock.lang.Specification

import static com.coditory.quark.i18n.Locales.EN
import static com.coditory.quark.i18n.Locales.EN_GB
import static com.coditory.quark.i18n.Locales.EN_US
import static com.coditory.quark.i18n.Locales.PL
import static com.coditory.quark.i18n.Locales.PL_PL

class ResolveMessageReferenceSpec extends Specification {
    def "should resolve message reference"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN, "msg", "\$company.name was established on 1988")
                    .addMessage(EN, "company.name", "ACME")
                    .build()
                    .getMessage(EN, "msg")
        then:
            message == "ACME was established on 1988"
    }

    def "should resolve a message with a transitive reference"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN, "msg", "\$company.name1 was established on 1988")
                    .addMessage(EN, "company.name1", ">\${company.name2}<")
                    .addMessage(EN, "company.name2", ">\${company.name3}<")
                    .addMessage(EN, "company.name3", "ACME")
                    .build()
                    .getMessage(EN, "msg")
        then:
            message == ">>ACME<< was established on 1988"
    }

    def "should resolve a message with a reference in braces and without them"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN, "msg", "a: \${a}, b: \${ b }, c: \$c")
                    .addMessage(EN, "a", "A")
                    .addMessage(EN, "b", "B")
                    .addMessage(EN, "c", "C")
                    .build()
                    .getMessage(EN, "msg")
        then:
            message == "a: A, b: B, c: C"
    }

    def "should throw error on unresolvable reference"() {
        when:
            I18nMessagePack.builder()
                    .addMessage(EN, "msg", "\$company.name was established on 1988")
                    .build()
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.getMessage() == "Reference not found: company.name"
    }

    def "should throw error on cyclic reference"() {
        when:
            I18nMessagePack.builder()
                    .addMessage(EN, "msg", "\$company.name1 was established on 1988")
                    .addMessage(EN, "company.name1", "\${company.name2}")
                    .addMessage(EN, "company.name2", "\${company.name3}")
                    .addMessage(EN, "company.name3", "\${company.name1}")
                    .build()
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.getMessage() == "Detected potential cyclic reference"
    }

    def "should throw error on a reference to itself"() {
        when:
            I18nMessagePack.builder()
                    .addMessage(EN, "msg", "\$msg")
                    .build()
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.getMessage() == "Detected potential cyclic reference"
    }

    def "should resolve references using fallback locales"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN_US, "msg", "a: \${a}, b: \${b}, c: \${c}, d: \${d}")
                    .addMessage(EN_US, "a", "a-en_US")
            // EN
                    .addMessage(EN, "a", "a-en")
                    .addMessage(EN, "b", "b-en")
            // PL_PL - default
                    .setDefaultLocale(PL_PL)
                    .addMessage(PL_PL, "a", "a-pl_PL")
                    .addMessage(PL_PL, "b", "b-pl_PL")
                    .addMessage(PL_PL, "c", "c-pl_PL")
            // PL - default's parent
                    .addMessage(PL, "a", "a-pl")
                    .addMessage(PL, "b", "b-pl")
                    .addMessage(PL, "c", "c-pl")
                    .addMessage(PL, "d", "d-pl")
            // EN_GB - control group - should not be used
                    .addMessage(EN_GB, "a", "a-en_GB")
                    .addMessage(EN_GB, "b", "b-en_GB")
                    .addMessage(EN_GB, "c", "c-en_GB")
                    .build()
                    .getMessage(EN_US, "msg")
        then:
            message == "a: a-en_US, b: b-en, c: c-pl_PL, d: d-pl"
    }

    def "should resolve references using fallback key prefix"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN_US, "msg", "a: \${a}, b: \${b}, c: \${c}, d: \${d}, e: \${e}")
                    .addMessage(EN_US, "common.a", "common.a-en_US")
                    .addMessage(EN_US, "common.b", "common.b-en_US")
            // EN
                    .addMessage(EN, "a", "a-en")
                    .addMessage(EN, "common.a", "common.a-en")
                    .addMessage(EN, "common.b", "common.b-en")
                    .addMessage(EN, "common.c", "common.c-en")
            // PL - default's parent
                    .addMessage(PL, "a", "a-pl")
                    .addMessage(PL, "b", "b-pl")
                    .addMessage(PL, "c", "c-pl")
                    .addMessage(PL, "d", "d-pl")
                    .addMessage(PL, "common.a", "common.a-pl")
                    .addMessage(PL, "common.b", "common.b-pl")
                    .addMessage(PL, "common.c", "common.c-pl")
                    .addMessage(PL, "common.d", "common.d-pl")
                    .addMessage(PL, "common.e", "common.e-pl")
            // common settings
                    .setDefaultLocale(PL_PL)
                    .addFallbackKeyPrefix("common")
                    .build()
                    .getMessage(EN_US, "msg")
        then:
            message == "a: a-en, b: common.b-en_US, c: common.c-en, d: d-pl, e: common.e-pl"
    }

    def "should prefix references from a loaded bundle"() {
        when:
            String message = I18nMessagePack.builder()
                    .addMessage(EN_US, "a", "a-root-en_US")
                    .addMessage(EN_US, "b", "b-root-en_US")
                    .addMessage(EN_US, "c", "c-root-en_US")
                    .addMessage(EN_US, "d", "d-en_US")
                    .addMessage(EN_US, "loaded.d", "d2-en_US")
                    .addLoader(InMemI18nLoader.of([
                            (I18nKey.of(EN_US, "msg")): "a: \$a, b: \$b, c: \$c, d: \$d",
                            (I18nKey.of(EN_US, "a"))  : "a-loaded-en_US",
                            (I18nKey.of(EN, "b"))     : "b-loaded-en"
                    ], I18nPath.of("loaded")))
                    .build()
                    .getMessage(EN_US, "loaded.msg")
        then:
            message == "a: a-loaded-en_US, b: b-loaded-en, c: c-root-en_US, d: d2-en_US"
    }
}
