package com.coditory.quark.i18n.loader;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

final class SimpleFilePattern {
    static boolean matches(String pattern, String input) {
        return matches(pattern, input, '/');
    }

    static boolean matches(String pattern, String input, char separator) {
        Pattern compiled = compileSimpleFilePattern(pattern, separator);
        return compiled.asMatchPredicate().test(input);
    }

    static Pattern compileSimpleFilePattern(String pattern, char separator) {
        String escapedSeparator = separator == '\\' ? "\\\\" : "" + separator;
        String regex = split(pattern, "\\*\\*+" + escapedSeparator)
                .map(chunk -> split(chunk, "\\*")
                        .map(subchunk -> subchunk.isEmpty() ? subchunk : Pattern.quote(subchunk))
                        .collect(joining("[^" + escapedSeparator + "]*"))
                ).collect(joining("(.*/)?"));
        return Pattern.compile(regex);
    }

    private static Stream<String> split(String input, String separator) {
        Stream<String> chunks = Arrays.stream(input.split(separator));
        return input.matches(".*" + separator + "$")
                ? Stream.concat(chunks, Stream.of(""))
                : chunks;
    }
}
