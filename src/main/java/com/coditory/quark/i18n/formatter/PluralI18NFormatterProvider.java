package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluralI18NFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "plural";

    @Override
    public I18nFormatter formatter(I18nMessageTemplates messages, List<String> args) {
        if (args.size() < 3) {
            throw new RuntimeException("Expected at least 3 arguments got: " + args);
        }
        if (args.size() % 2 != 1) {
            throw new RuntimeException("Expected odd number of arguments got: " + args);
        }
        List<PluralIntervals> intervals = new ArrayList<>();
        for (int i = 0; i < args.size(); i += 2) {
            Integer lower = i > 0
                    ? Integer.parseInt(args.get(i - 1))
                    : null;
            Integer upper = i + 1 < args.size()
                    ? Integer.parseInt(args.get(i + 1))
                    : null;
            String label = args.get(i);
            if (lower != null && upper != null && lower > upper) {
                throw new RuntimeException("Expected increasing values in plural formatter.");
            }
            intervals.add(new PluralIntervals(lower, upper, label));
        }
        return new PluralI18NFormatter(intervals);
    }

    private record PluralIntervals(Integer min, Integer max, String label) {
        boolean isIn(Integer value) {
            boolean geMin = min == null || min <= value;
            boolean ltMax = max == null || max > value;
            return geMin && ltMax;
        }

        boolean isExact(Integer value) {
            boolean eqMin = min != null && min.equals(value);
            boolean eqMax = max != null && max.equals(value);
            return eqMin && eqMax;
        }
    }

    static class PluralI18NFormatter implements I18nFormatter {
        private final List<PluralIntervals> intervals;

        public PluralI18NFormatter(List<PluralIntervals> intervals) {
            this.intervals = intervals;
        }

        @Override
        public String format(Object value) {
            if (!(value instanceof Number)) {
                return Objects.toString(value);
            }
            Integer numValue = ((Number) value).intValue();
            return intervals.stream()
                    .filter(i -> i.isExact(numValue) || i.isIn(numValue))
                    .map(PluralIntervals::label)
                    .findFirst()
                    .orElseGet(() -> Objects.toString(value));
        }
    }
}
