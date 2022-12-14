package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;

import java.util.Map;

public interface WatchableI18nLoader extends I18nLoader {
    void addChangeListener(I18nLoaderChangeListener listener);

    void startWatching();

    default void startWatching(I18nLoaderChangeListener listener) {
        addChangeListener(listener);
        startWatching();
    }

    void stopWatching();

    interface I18nLoaderChangeListener {
        void onChange(Map<I18nKey, String> entries);
    }
}
