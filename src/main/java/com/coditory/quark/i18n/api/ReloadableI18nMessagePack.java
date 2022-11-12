package com.coditory.quark.i18n.api;

import com.coditory.quark.i18n.I18nMessages;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

public class ReloadableI18nMessagePack implements I18nMessagePack {
    private final AtomicReference<I18nMessagePack> messagePack = new AtomicReference<>();

    public ReloadableI18nMessagePack(I18nMessagePack messagePack) {
        requireNonNull(messagePack);
        this.messagePack.set(messagePack);
    }

    @Override
    public I18nMessages forLocale(Locale locale) {
        return messagePack.get()
                .forLocale(locale);
    }

    public void setMessagePack(I18nMessagePack messagePack) {
        requireNonNull(messagePack);
        this.messagePack.set(messagePack);
    }
}
