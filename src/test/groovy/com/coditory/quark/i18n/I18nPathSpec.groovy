package com.coditory.quark.i18n

import spock.lang.Specification
import spock.lang.Unroll

class I18nPathSpec extends Specification {
    @Unroll
    def "should parse I18nPath: #value"() {
        expect:
            I18nPath.of(value) == expected
        where:
            value   || expected
            "a.b.c" || I18nPath.of("a", "b", "c")
            "a.b"   || I18nPath.of("a", "b")
            "a"     || I18nPath.of("a")
            ""      || I18nPath.root()
    }

    @Unroll
    def "should throw error for invalid I18nPath: #value"() {
        when:
            I18nPath.of(value)
        then:
            thrown(IllegalArgumentException)
        where:
            value << [null, " ", "\n", ".", "..", ". .", " .", ". ", "a&b", "a/b"]
    }

    @Unroll
    def "should extract parent I18nPath from #path"() {
        expect:
            path.parentOrRoot() == expected
        where:
            path                       || expected
            I18nPath.of("a", "b", "c") || I18nPath.of("a", "b")
            I18nPath.of("a", "b")      || I18nPath.of("a")
            I18nPath.of("a")           || I18nPath.root()
            I18nPath.root()            || I18nPath.root()
    }

    @Unroll
    def "should create child I18nPath for '#path' and '#child'"() {
        expect:
            path.child(child) == expected
        where:
            path                       | child || expected
            I18nPath.of("a", "b", "c") | "x"   || I18nPath.of("a", "b", "c", "x")
            I18nPath.of("a", "b")      | "x"   || I18nPath.of("a", "b", "x")
            I18nPath.of("a")           | "x"   || I18nPath.of("a", "x")
            I18nPath.of("a")           | "x.y" || I18nPath.of("a", "x", "y")
            I18nPath.of("a")           | ""    || I18nPath.of("a")
            I18nPath.root()            | "x"   || I18nPath.of("x")
            I18nPath.root()            | ""    || I18nPath.root()
    }
}