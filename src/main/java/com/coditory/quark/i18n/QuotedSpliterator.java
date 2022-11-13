package com.coditory.quark.i18n;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class QuotedSpliterator {
    private QuotedSpliterator() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static List<String> splitBy(String expression, char separator) {
        expectNonNull(expression, "expression");
        List<String> result = new ArrayList<>();
        boolean escaped = false;
        Character quote = null;
        boolean preserveQuote = false;
        StringBuilder chunk = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (escaped) {
                if ('\'' == c || '"' == c) {
                    chunk.append(c);
                } else {
                    throw new RuntimeException("Could not escape: \"" + c + "\" in expression: \"" + expression + "\"");
                }
                escaped = false;
            } else if ('\\' == c) {
                escaped = true;
            } else if ('\'' == c || '"' == c) {
                if (quote == null) {
                    quote = c;
                    preserveQuote = chunk.length() != 0;
                    if (preserveQuote) {
                        chunk.append(c);
                    }
                } else if (quote == c) {
                    quote = null;
                    if (preserveQuote) {
                        chunk.append(c);
                    } else {
                        result.add(chunk.toString());
                        chunk = new StringBuilder();
                    }
                }
            } else if (quote != null) {
                chunk.append(c);
            } else if (separator == c) {
                if (chunk.length() > 0) {
                    result.add(chunk.toString().trim());
                    chunk = new StringBuilder();
                }
            } else if (c != ' ' || chunk.length() > 0) {
                chunk.append(c);
            }
        }
        if (chunk.length() > 0) {
            result.add(chunk.toString().trim());
        }
        return result;
    }
}
