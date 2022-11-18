package com.coditory.quark.i18n.loader

import spock.lang.Specification
import spock.lang.Unroll

class SimpleFilePatternSpec extends Specification {
    @Unroll
    def "should match file path #path with pattern: #pattern"() {
        given:

            when:
                then:
                    where:
                        pattern | input
    }
}
