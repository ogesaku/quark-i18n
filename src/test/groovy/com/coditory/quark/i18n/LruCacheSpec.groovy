package com.coditory.quark.i18n

import spock.lang.Specification

class LruCacheSpec extends Specification {
    def "should retrieve values by key"() {
        given:
            LruCache<String, String> cache = new LruCache<>()
        and:
            cache.put("a", "A")
            cache.put("b", "B")
            cache.put("c", "C")
        expect:
            cache.get("a") == "A"
            cache.get("b") == "B"
            cache.get("c") == "C"
    }

    def "should override value by key"() {
        given:
            LruCache<String, String> cache = new LruCache<>()
        and:
            cache.put("a", "A")
            cache.put("b", "B")
            cache.put("c", "C")
        when:
            cache.put("a", "A2")
            cache.put("b", "B2")
        expect:
            cache.get("a") == "A2"
            cache.get("b") == "B2"
            cache.get("c") == "C"
    }

    def "should drop first added item"() {
        given:
            LruCache<String, String> cache = new LruCache<>(3)
        and:
            cache.put("a", "A")
            cache.put("b", "B")
            cache.put("c", "C")
            cache.put("d", "D")
        expect:
            cache.get("a") == null
            cache.get("b") == "B"
            cache.get("c") == "C"
            cache.get("d") == "D"
    }

    def "should drop least recently read item"() {
        given:
            LruCache<String, String> cache = new LruCache<>(3)
        and:
            cache.put("a", "A")
            cache.put("b", "B")
            cache.put("c", "C")
        when:
            cache.get("a")
            cache.put("d", "D")
        expect:
            cache.get("a") == "A"
            cache.get("b") == null
            cache.get("c") == "C"
            cache.get("d") == "D"
    }

    def "should drop least recently written item"() {
        given:
            LruCache<String, String> cache = new LruCache<>(3)
        and:
            cache.put("a", "A")
            cache.put("b", "B")
            cache.put("c", "C")
        when:
            cache.put("a", "A2")
            cache.put("d", "D")
        expect:
            cache.get("a") == "A2"
            cache.get("b") == null
            cache.get("c") == "C"
            cache.get("d") == "D"
    }
}
