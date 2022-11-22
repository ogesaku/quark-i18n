package com.coditory.quark.i18n.loader;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

final class SimpleFilePattern {
    static boolean matches(String pattern, String input) {
        return matches(pattern, input, '/');
    }

    static boolean matches(String pattern, String input, char separator) {
        Pattern compiled = compile(pattern, separator);
        return compiled.asMatchPredicate().test(input);
    }

    static Pattern compile(String pattern) {
        return compile(pattern, '/');
    }

    static Pattern compile(String pattern, char separator) {
        String escapedSeparator = Objects.equals(separator, '\\') ? "\\\\" : "" + separator;
        String regex = split(pattern, "\\*\\*+" + escapedSeparator)
                .map(chunk -> split(chunk, "\\*")
                        .map(subChunk -> subChunk.isEmpty() ? subChunk : Pattern.quote(subChunk))
                        .collect(joining("[^" + escapedSeparator + "]*"))
                ).collect(joining("(.*/)?"));
        return Pattern.compile(regex);
    }

    static String extractBaseDir(String pattern) {
        return extractBaseDir(pattern, '/');
    }

    static String extractBaseDir(String pattern, char separator) {
        int dirEnd = pattern.indexOf("**" + separator);
        if (dirEnd < 0) {
            return pattern;
        }
        while (dirEnd > 0 && pattern.charAt(dirEnd) != separator) dirEnd--;
        if (dirEnd > 0 && pattern.charAt(dirEnd) == separator) dirEnd--;
        return pattern.substring(0, dirEnd);
    }

    private static Stream<String> split(String input, String separator) {
        Stream<String> chunks = Arrays.stream(input.split(separator));
        return input.matches(".*" + separator + "$")
                ? Stream.concat(chunks, Stream.of(""))
                : chunks;
    }
}
