package com.coditory.quark.i18n.loader

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.quark.i18n.loader.SimpleFilePattern.matches

class SimpleFilePatternSpec extends Specification {
    @Unroll
    def "should match file path #input with pattern: #pattern"() {
        expect:
            matches(pattern, input)
        where:
            pattern          | input
            // absolute
            "/abc/**/*.json" | "/abc/xxx.json"
            "/abc/**/*.json" | "/abc/def/xxx.json"
            "/abc/**/*.json" | "/abc/def/ghi/xxx.json"
            "/abc/*.json"    | "/abc/def.json"
            // relative
            "abc/**/*.json"  | "abc/xxx.json"
            "abc/**/*.json"  | "abc/def/xxx.json"
            "abc/**/*.json"  | "abc/def/ghi/xxx.json"
            "abc/*.json"     | "abc/def.json"
            // no prefix
            "**/*.json"      | "xxx.json"
            "**/*.json"      | "abc/def/xxx.json"
            "**/*.json"      | "/abc/xxx.json"
            // one star
            "/abc/*.json"    | "/abc/.json"
            "/abc/x*.json"   | "/abc/x.json"
            "/abc/x*.json"   | "/abc/xyz.json"
            "/abc/x*z.json"  | "/abc/xz.json"
            "/abc/x*z.json"  | "/abc/xyyyz.json"
            "/abc/x*.json"   | "/abc/x.json"
            "/abc/*.json"    | "/abc/x.json.json"
            "/abc/*.*.json"  | "/abc/x.xml.json"
    }

    @Unroll
    def "should not match file path #input with pattern: #pattern"() {
        expect:
            !matches(pattern, input)
        where:
            pattern                | input
            "/abc/[a-z]/**/*.json" | "/abc/a/xxx.json"
            "/abc/**/*.json"       | "/abc.json"
            "/abc/**/*.json"       | "/abc/def/xxx.json2"
            "/abc/**/*.json"       | "abc/def/xxx.json2"
            "abc/**/*.json"        | "/abc/xxx.json"
            "abc/*.json"           | "abc/def/xxx.json"
            "abc/*.json"           | "abc/xxx.json2"
            "/abc/**/x*.json"      | "/abc/def/yyy.json"
            "/abc/**/x*z.json"     | "/abc/def/xyzy.json"
            "/abc/**/x*.json"      | "/abc/def/yx.json"
    }
}
