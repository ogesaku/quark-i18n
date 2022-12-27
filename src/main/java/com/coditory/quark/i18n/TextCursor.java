package com.coditory.quark.i18n;

final class TextCursor {
    public static TextCursor beginningOf(String text) {
        return new TextCursor(0, text.length(), text);
    }

    private final String text;
    private final int lowerBound;
    private final int upperBound;
    private int pos;

    public TextCursor(int lowerBound, int upperBound, String text) {
        if (lowerBound < 0) {
            throw new IllegalArgumentException("Expected non negative cursor lowerBound. Got: " + lowerBound);
        }
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Expected cursor lowerBound <= upperBound. Got lowerBound=" + lowerBound + " upperBound=" + upperBound);
        }
        if (lowerBound > text.length()) {
            throw new IllegalArgumentException("Expected cursor lowerBound <= chars.length(). Got lowerBound=" + lowerBound + " chars.length()=" + text.length());
        }
        if (upperBound > text.length()) {
            throw new IllegalArgumentException("Expected cursor upperBound <= chars.length(). Got lowerBound=" + upperBound + " chars.length()=" + text.length());
        }
        this.text = text;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.pos = lowerBound;
    }

    public int getPosition() {
        return pos;
    }

    public char getFullText() {
        return text.charAt(pos);
    }

    public int length() {
        return upperBound - pos + 1;
    }

    public boolean startsWithAny(char... any) {
        if (pos == upperBound) {
            return false;
        }
        char next = text.charAt(pos + 1);
        for (char a : any) {
            if (next == a) {
                return true;
            }
        }
        return false;
    }

    public boolean startsWithAny(String... any) {
        if (pos == upperBound) {
            return false;
        }
        for (String a : any) {
            if (startsWith(a)) {
                return true;
            }
        }
        return false;
    }

    public boolean startsWith(String value) {
        if (text.length() < value.length() + pos) {
            return false;
        }
        boolean match = true;
        int i = 0;
        while (i < value.length() && match) {
            match = value.charAt(i) == text.charAt(pos + i);
            ++i;
        }
        return match && i == value.length();
    }

    public boolean endsWith(String value) {
        if (text.length() < value.length() + pos) {
            return false;
        }
        boolean match = true;
        int i = 0;
        while (i < value.length() && match) {
            match = value.charAt(i) == text.charAt(upperBound - value.length() + i);
            ++i;
        }
        return match && i == value.length();
    }

    public char peekChar() {
        return text.charAt(pos);
    }

    public char nextChar() {
        return nextChar(1);
    }

    public char prevChar() {
        if (pos - 1 < lowerBound) {
            throw new IllegalArgumentException("Out of lowerbound");
        }
        pos -= 1;
        return text.charAt(pos + 1);
    }

    public char nextChar(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Expected steps > 0");
        }
        if (pos + steps > upperBound) {
            throw new IllegalArgumentException("Out of upperbound");
        }
        pos += steps;
        return text.charAt(pos - 1);
    }

    public boolean hasNextChar() {
        return this.pos == this.upperBound;
    }

    @Override
    public String toString() {
        return "["
            + this.lowerBound + '>'
            + this.pos + '>'
            + this.upperBound +
            ']';
    }
}
