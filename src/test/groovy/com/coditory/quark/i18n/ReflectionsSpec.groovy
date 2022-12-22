package com.coditory.quark.i18n

import spock.lang.Specification

class ReflectionsSpec extends Specification {
    def "should find all interfaces and classes"() {
        when:
            List<Class<?>> types = com.coditory.quark.eventbus.Reflections.getAllInterfacesAndClasses(A)
        then:
            types == [
                    A, IA, IA2,
                    B, IB, IC2,
                    C, IC,
                    GroovyObject,
                    Object
            ]
    }

    class A extends B implements IA, IA2 {}

    class B extends C implements IB {}

    class C implements IC {}

    interface IA {}

    interface IA2 {}

    interface IB extends IC2 {}

    interface IC extends IC2 {}

    interface IC2 {}
}