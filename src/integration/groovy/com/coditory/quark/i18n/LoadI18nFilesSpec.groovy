package com.coditory.quark.i18n

import com.coditory.quark.i18n.base.UsesFiles
import com.coditory.quark.i18n.loader.I18nMessagesFileLoader
import com.coditory.quark.i18n.loader.I18nMessagesLoader
import spock.lang.Specification

import static com.coditory.quark.i18n.Locales.EN_US
import static com.coditory.quark.i18n.Locales.PL_PL

class LoadI18nFilesSpec extends Specification implements UsesFiles {
    def "should load i18n files"() {
        given:
            writeClasspathFile("i18n/base.yml", """
            glossary:
              site:
                en: Acme site
            """)
            writeClasspathFile("i18n/homepage.yml", """
            homepage:
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
            I18nMessagesLoader loader = I18nMessagesFileLoader.builder()
                    .scanClassPath(classLoader)
                    .scanPathPattern("i18n/*")
                    .build()
            I18nMessagePack i18nMessagePack = I18nMessagePack.builder()
                    .addLoader(loader)
                    .build()
        then:
            I18nMessages messagesPl = i18nMessagePack.localize(PL_PL)
            messagesPl.getMessage("user.title") == "Użytkownik"
            messagesPl.getMessage("homepage.title") == "Strona domowa"

        and:
            I18nMessages messagesEn = i18nMessagePack.localize(EN_US)
            messagesEn.getMessage("user.title") == "User"
            messagesEn.getMessage("homepage.title") == "Homepage"
    }
}
