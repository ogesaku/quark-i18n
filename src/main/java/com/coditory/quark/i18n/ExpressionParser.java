package com.coditory.quark.i18n;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ExpressionParser {
    private final FilterResolver filterResolver;

    ExpressionParser(FilterResolver filterResolver) {
        expectNonNull(filterResolver, "filterResolver");
        this.filterResolver = filterResolver;
    }

    Expression parseText(String input) {
        TextCursor cursor = TextCursor.beginningOf(input);
        return parseText(cursor, false);
    }

    private Expression parseText(TextCursor cursor, boolean nested) {
        if (nested && !cursor.startsWithAny("(")) {
            throw new RuntimeException("Expected nested text to start with \"(\"");
        } else if (nested) {
            cursor.nextChar();
        }
        List<Expression> expressions = new ArrayList<>();
        StringBuilder chunk = new StringBuilder();
        boolean closed = false;
        while (!cursor.hasNextChar()) {
            char c = cursor.nextChar();
            if (c == '\\' && cursor.startsWithAny("${")) {
                chunk.append(cursor.nextChar());
                chunk.append(cursor.nextChar());
            } else if (c == '\\' && cursor.startsWithAny('{', '}')) {
                chunk.append(cursor.nextChar());
            } else if (c == '\\' && nested && cursor.startsWithAny('(', ')')) {
                chunk.append(cursor.nextChar());
            } else if (c == ')' && nested) {
                closed = true;
                break;
            } else if (c == '{' || cursor.startsWith("${")) {
                expressions.add(StaticExpression.parse(chunk));
                chunk = new StringBuilder();
                cursor.prevChar();
                Expression expression = parseExpression(cursor);
                expressions.add(expression);
            } else if (c == '}') {
                throw new RuntimeException("Unmatched expression closing at index " + cursor.getPosition() + "\" in \"" + cursor.getFullText() + "\"");
            } else {
                chunk.append(c);
            }
        }
        if (nested && !closed) {
            throw new IllegalArgumentException("Unclosed expression. Missing: ')'");
        }
        expressions.add(StaticExpression.parse(chunk));
        return CompositeExpression.of(expressions);
    }

    private Expression parseExpression(TextCursor cursor) {
        if (!cursor.startsWithAny("{", "${")) {
            throw new RuntimeException("Expected expression to start with \"{\" or \"${\"");
        }
        ExpressionBuilder builder;
        if (cursor.startsWithAny("${")) {
            builder = ExpressionBuilder.reference(filterResolver);
            cursor.nextChar(2);
        } else {
            builder = ExpressionBuilder.argument(filterResolver);
            cursor.nextChar();
        }
        StringBuilder chunk = new StringBuilder();
        while (!cursor.hasNextChar()) {
            char c = cursor.nextChar();
            if (c == '\\' && cursor.startsWithAny("${")) {
                chunk.append(cursor.nextChar());
                chunk.append(cursor.nextChar());
            } else if (c == '\\' && cursor.startsWithAny('{', '}')) {
                chunk.append(cursor.nextChar());
            } else if (c == '\\' && cursor.startsWithAny('(', ')')) {
                chunk.append(cursor.nextChar());
            } else if (cursor.startsWith("${") || c == '{') {
                builder.addToken(chunk);
                chunk = new StringBuilder();
                cursor.prevChar();
                Expression expression = parseExpression(cursor);
                builder.addToken(expression);
            } else if (c == '}') {
                builder.addToken(chunk);
                return builder.build();
            } else if (c == '|') {
                builder.addToken(chunk);
                chunk = new StringBuilder();
                builder.pipe();
            } else if (c == '(') {
                builder.addToken(chunk);
                chunk = new StringBuilder();
                cursor.prevChar();
                Expression expression = parseText(cursor, true);
                builder.addToken(expression);
            } else if (Character.isWhitespace(c)) {
                builder.addToken(chunk);
                chunk = new StringBuilder();
            } else {
                chunk.append(c);
            }
        }
        throw new IllegalArgumentException("Unclosed expression. Missing: '}'");
    }

}
