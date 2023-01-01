package com.coditory.quark.i18n.loader;

import java.util.List;

public interface WatchableI18nLoader extends I18nLoader {
    void addChangeListener(I18nLoaderChangeListener listener);

    void startWatching();

    default void startWatching(I18nLoaderChangeListener listener) {
        addChangeListener(listener);
        startWatching();
    }

    void stopWatching();

    interface I18nLoaderChangeListener {
        void onChange(List<I18nTemplatesBundle> entries);
    }
}
