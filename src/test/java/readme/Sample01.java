package readme;

import com.coditory.quark.i18n.I18nMessagePack;
import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.Reloadable18nMessagePack;
import com.coditory.quark.i18n.loader.I18nKeyParser;
import com.coditory.quark.i18n.loader.I18nMessagesFileLoader;

public class Sample01 {
    public static void main(String[] args) {
        I18nMessagesFileLoader loader = I18nMessagesFileLoader.builder()
                .scanClassPath()
                .scanLocation("/**/i18n-{key}.yml")
                .staticKeyPrefix("page")
                .build();
        Reloadable18nMessagePack i18nMessagePack = I18nMessagePack.builder()
                .addLoader(loader)
                .buildReloadable();
        I18nMessages messages = i18nMessagePack.localize("pl");

        String message = messages.getMessage("hello");
        System.out.println("Message: " + message);
    }
}
