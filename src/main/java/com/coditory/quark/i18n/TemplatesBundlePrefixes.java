package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nTemplatesBundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TemplatesBundlePrefixes {
    static List<I18nTemplatesBundle> prefix(List<I18nTemplatesBundle> bundles) {
        return bundles.stream()
                .map(TemplatesBundlePrefixes::prefix)
                .toList();
    }

    static private I18nTemplatesBundle prefix(I18nTemplatesBundle bundle) {
        I18nPath prefix = bundle.prefix();
        if (prefix == null || prefix.isRoot()) {
            return bundle;
        }
        Map<I18nKey, String> mapped = new HashMap<>();
        for (Map.Entry<I18nKey, String> entry : bundle.templates().entrySet()) {
            I18nKey prefixed = entry.getKey().prefixPath(prefix);
            mapped.put(prefixed, entry.getValue());
        }
        return new I18nTemplatesBundle(mapped, prefix);
    }
}
