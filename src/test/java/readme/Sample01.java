package readme;

import com.coditory.quark.i18n.I18nMessagePack;
import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.Reloadable18nMessagePack;
import com.coditory.quark.i18n.loader.I18nMessagesFileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sample01 {
    public static void main(String[] args) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.out.println("resources: " + toList(classLoader.getResources("a/b/c/homepage/i18n.yml").asIterator()));
        System.out.println("DOT   resources: " + toList(classLoader.getResources(".").asIterator()));
        System.out.println("SLASH resources: " + toList(classLoader.getResources("/").asIterator()));
        System.out.println("DOT   resource: " + classLoader.getResource("."));
        System.out.println("SLASH resource: " + classLoader.getResource("/"));

        I18nMessagesFileLoader pageI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("/**/{prefix}/i18n.yml")
                .build();
        System.out.println("PAGE: \n" + pageI18nLoader.load());
        I18nMessagesFileLoader baseI18nLoader = I18nMessagesFileLoader.builder()
                .scanPathPattern("/i18n/*")
                .build();
        System.out.println("BASE: \n" + baseI18nLoader.load());
        Reloadable18nMessagePack i18nMessagePack = I18nMessagePack.builder()
                .scanClassPath("/**/{prefix}/i18n.yml")
                .scanClassPath("/i18n/*")
                .buildReloadable();
        I18nMessages messages = i18nMessagePack.localize("pl");

        String message = messages.getMessage("homepage.hello");
        System.out.println("Message: " + message);
    }

    static <T> List<T> toList(Iterator<T> iterator) {
        List<T> actualList = new ArrayList<>();
        while (iterator.hasNext()) {
            actualList.add(iterator.next());
        }
        return actualList;
    }
}
