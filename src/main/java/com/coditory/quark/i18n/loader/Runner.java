package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nMessagePack;
import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.Reloadable18nMessagePack;

import java.io.IOException;
import java.util.Set;

public class Runner {
    public static void main2(String[] args) {
        I18nMessagesFileLoader pageI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("src/main/resources/**/{prefix}/i18n.yml")
                .build();
        System.out.println("PAGE: \n" + pageI18nLoader.load());

        I18nMessagesFileLoader baseI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("src/main/resources/i18n/*")
                .build();
        System.out.println("BASE: \n" + baseI18nLoader.load());

        Reloadable18nMessagePack i18nMessagePack = I18nMessagePack.builder()
                .scanFileSystem("src/main/resources/**/{prefix}/i18n.yml")
                .scanFileSystem("src/main/resources/i18n/*")
                .buildReloadable();
        I18nMessages messages = i18nMessagePack.localize("pl");
        String message = messages.getMessage("homepage.hello");
        System.out.println("Message: " + message);
    }

    public static void main(String[] args) throws IOException {
        ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
        Set<ClassPath.ResourceInfo> resources = cp.getResources("/a/b");
        System.out.println("Resources: " + resources);


        I18nMessagesFileLoader pageI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("**/{prefix}/i18n.yml")
                .build();
        System.out.println("PAGE: \n" + pageI18nLoader.load());
        I18nMessagesFileLoader baseI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("i18n/*")
                .build();
        System.out.println("BASE: \n" + baseI18nLoader.load());
        Reloadable18nMessagePack i18nMessagePack = I18nMessagePack.builder()
                .scanClassPath("**/{prefix}/i18n.yml")
                .scanClassPath("i18n/*")
                .buildReloadable();
        I18nMessages messages = i18nMessagePack.localize("pl");

        String message = messages.getMessage("homepage.hello");
        System.out.println("Message: " + message);
    }
}
