package com.coditory.quark.i18n;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ExpressionSpliterator {
    private ExpressionSpliterator() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static List<String> splitBy(String expression, char separator) {
        expectNonNull(expression, "expression");
        List<String> result = new ArrayList<>();
        boolean escaped = false;
        StringBuilder chunk = new StringBuilder();
        int stack = 0;
        for (char c : expression.toCharArray()) {
            if (escaped) {
                if ('{' == c || '}' == c || separator == c) {
                    chunk.append(c);
                } else {
                    throw new RuntimeException("Could not escape: \"" + c + "\" in expression: \"" + expression + "\"");
                }
                escaped = false;
            } else if ('\\' == c) {
                escaped = true;
            } else if ('{' == c) {
                chunk.append(c);
                stack++;
            } else if ('}' == c) {
                chunk.append(c);
                stack--;
            } else if (stack == 0 && separator == c) {
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
