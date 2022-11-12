package com.coditory.quark.i18n;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class Currencies {
    /**
     * US Dollar
     */
    public static final Currency USD = Currency.getInstance("USD");

    /**
     * Euro
     */
    public static final Currency EUR = Currency.getInstance("EUR");

    /**
     * Canadian Dollar
     */
    public static final Currency CAD = Currency.getInstance("CAD");

    /**
     * Yen
     */
    public static final Currency JPY = Currency.getInstance("JPY");

    /**
     * Pound Sterling
     */
    public static final Currency GBP = Currency.getInstance("GBP");

    /**
     * Yuan Renminbi
     */
    public static final Currency CNY = Currency.getInstance("CNY");

    /**
     * Polish Zloty
     */
    public static final Currency PLN = Currency.getInstance("PLN");

    private Currencies() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    public static Currency parseCurrency(String value) {
        expectNonNull(value, "value");
        Currency currency;
        try {
            currency = Currency.getInstance(value.toUpperCase(Locale.ROOT).trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse Currency: '" + value + "'");
        }
        boolean isAvailable = Currency.getAvailableCurrencies()
                .contains(currency);
        if (!isAvailable) {
            throw new IllegalArgumentException("Currency not available: '" + value + "'");
        }
        return currency;
    }

    public static Currency parseCurrencyOrNull(String value) {
        try {
            return parseCurrency(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Currency parseCurrencyOrDefault(String value, Currency defaultValue) {
        Currency result = parseCurrencyOrNull(value);
        return result == null ? defaultValue : result;
    }

    public static Optional<Currency> parseCurrencyOrEmpty(String value) {
        return Optional.ofNullable(parseCurrencyOrNull(value));
    }
}
