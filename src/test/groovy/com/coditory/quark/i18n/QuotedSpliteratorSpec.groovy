package com.coditory.quark.i18n

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.QuotedSpliterator.splitBy

class QuotedSpliteratorSpec extends Specification {
    @Unroll
    def "should split by a space: #text"() {
        when:
            List<String> result = splitBy(text, " ".toCharArray()[0])
        then:
            result == expected
        where:
            text                      | expected
            "a b c"                   | ["a", "b", "c"]
            " a  b   c    "           | ["a", "b", "c"]
            "a 'b c' d"               | ["a", "b c", "d"]
            "a ' b c ' d"             | ["a", " b c ", "d"]
            "a 'b \\' x \\' c' d"     | ["a", "b ' x ' c", "d"]
            "a \"b \\\" x \\\" c\" d" | ["a", "b \" x \" c", "d"]
    }

    @Unroll
    def "should split by a pipe character: #text"() {
        when:
            List<String> result = splitBy(text, "|".toCharArray()[0])
        then:
            result == expected
        where:
            text              | expected
            "a|b|c"           | ["a", "b", "c"]
            " a | b |  c    " | ["a", "b", "c"]
            "x | a 'b c'"     | ["x", "a 'b c'"]
            "x | a \"b c\""   | ["x", "a \"b c\""]
            "x | a 'b | c'"   | ["x", "a 'b | c'"]
            "x | 'b | c'"     | ["x", "b | c"]
            "x | 'b \\'| c'"  | ["x", "b '| c"]
    }
}
