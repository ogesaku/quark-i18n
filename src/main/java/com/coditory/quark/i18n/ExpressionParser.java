package com.coditory.quark.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ExpressionParser {
    private final FormatterResolver formatterResolver;

    ExpressionParser(FormatterResolver formatterResolver) {
        expectNonNull(formatterResolver, "formatterResolver");
        this.formatterResolver = formatterResolver;
    }

    Expression parse(String expression) {
        expectNonNull(expression, "expression");
        ExpressionContext context = new ExpressionContext(formatterResolver, this);
        List<Expression> expressions = new ArrayList<>();
        boolean formatterExpression = false;
        boolean referenceExpression = false;
        boolean escaped = false;
        Set<Character> escapable = Set.of('$', '{', '}');
        StringBuilder chunk = new StringBuilder();
        char[] chars = expression.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (escaped) {
                if (!escapable.contains(c)) {
                    throw new RuntimeException("Could not escape: \"" + c + "\" in expression: \"" + expression + "\"");
                }
                escaped = false;
                chunk.append(c);
            } else if ('\\' == c) {
                escaped = true;
            } else if (startsWith(chars, i, "${")) {
                if (chunk.length() > 0) {
                    StaticExpression parsed = StaticExpression.parse(chunk.toString());
                    expressions.add(parsed);
                    chunk = new StringBuilder();
                }
                referenceExpression = true;
                i += 1;
            } else if ('{' == c) {
                if (chunk.length() > 0) {
                    StaticExpression parsed = StaticExpression.parse(chunk.toString());
                    expressions.add(parsed);
                    chunk = new StringBuilder();
                }
                formatterExpression = true;
            } else if ('}' == c) {
                if (formatterExpression) {
                    if (chunk.length() > 0) {
                        FormatterExpression parsed = FormatterExpression.parse(chunk.toString().trim(), context);
                        expressions.add(parsed);
                        chunk = new StringBuilder();
                    }
                    formatterExpression = false;
                } else if (referenceExpression) {
                    if (chunk.length() > 0) {
                        ReferenceExpression parsed = ReferenceExpression.parse(chunk.toString().trim());
                        expressions.add(parsed);
                        chunk = new StringBuilder();
                    }
                    referenceExpression = false;
                } else {
                    throw new RuntimeException("Unopened expression in template: \"" + expression + "\"");
                }
            } else {
                chunk.append(c);
            }
        }
        if (chunk.length() > 0) {
            StaticExpression parsed = StaticExpression.parse(chunk.toString());
            expressions.add(parsed);
        }
        if (expressions.isEmpty()) {
            return StaticExpression.parse("");
        }
        if (expressions.size() == 1) {
            return expressions.get(0);
        }
        return CompositeExpression.of(expressions);
    }

    private static boolean startsWith(char[] c, int index, String value) {
        if (c.length < value.length() + index) {
            return false;
        }
        boolean match = true;
        int i = 0;
        while (i < value.length() && match) {
            match = value.charAt(i) == c[index + i];
            ++i;
        }
        return match && i == value.length();
    }
}
