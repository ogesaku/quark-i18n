package com.coditory.quark.i18n

import spock.lang.Specification

class PreconditionsSpec extends Specification {
    def "should fail non-null check"() {
        when:
            Preconditions.expectNonNull(null, "userName")
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.message == "Expected non-null value: userName"
    }

    def "should pass non-null check"() {
        given:
            String name = "John"
        expect:
            Preconditions.expectNonNull(name, "userName") == name
    }
}
