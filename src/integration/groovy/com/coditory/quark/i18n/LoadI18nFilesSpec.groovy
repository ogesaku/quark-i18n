package com.coditory.quark.i18n

import com.coditory.quark.i18n.base.UsesFiles
import spock.lang.Specification

class LoadI18nFilesSpec extends Specification implements UsesFiles {
    def "should load i18n files"() {
        given:
            writeClasspathFile("i18n/base.yml", """
            glossary:
              site:
                en: Acme site
            """)
            writeClasspathFile("i18n/homepage.yml", """
            homepage
              title:
                en: Homepage
                pl: Strona domowa
            """)
            writeClasspathFile("i18n/user.yml", """
            user:
              title:
                en: User
                pl: Użytkownik
            """)
        when:

        then:
            config.toMap() == [
                    a: "BASE",
                    b: "PROFILE",
                    c: "EXTERNAL",
                    d: "ARGS",
            ]
    }
}
