package com.coditory.quark.i18n

import spock.lang.Specification
import spock.lang.Unroll

class I18nKeySpec extends Specification {
    @Unroll
    def "should expose i18nKey values"() {
        when:
            I18nKey key = I18nKey.of(Locales.EN_US, "a.b.c")
        then:
            key.toShortString() == "en_US:a.b.c"
            key.pathValue() == "a.b.c"
            key.path() == I18nPath.of("a", "b", "c")
            key.locale() == Locales.EN_US
    }

    @Unroll
    def "should create i18nKey with different locale"() {
        given:
            I18nKey key = I18nKey.of(Locales.EN_US, "a.b.c")
        expect:
            key.withLocale(Locales.PL) == I18nKey.of(Locales.PL, "a.b.c")
    }

    @Unroll
    def "should create i18nKey with child path"() {
        given:
            I18nKey key = I18nKey.of(Locales.EN_US, "a.b.c")
        expect:
            key.child("x.y") == I18nKey.of(Locales.EN_US, "a.b.c.x.y")
    }

    @Unroll
    def "should create i18nKey with prefixed path"() {
        given:
            I18nKey key = I18nKey.of(Locales.EN_US, "a.b.c")
        expect:
            key.prefixPath("x.y") == I18nKey.of(Locales.EN_US, "x.y.a.b.c")
    }

    @Unroll
    def "should create i18nKey with different path"() {
        given:
            I18nKey key = I18nKey.of(Locales.EN_US, "a.b.c")
        expect:
            key.withPath("x.y") == I18nKey.of(Locales.EN_US, "x.y")
    }
}