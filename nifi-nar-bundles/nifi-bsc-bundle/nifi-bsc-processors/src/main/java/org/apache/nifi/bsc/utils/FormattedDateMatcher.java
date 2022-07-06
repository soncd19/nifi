package org.apache.nifi.bsc.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * Created by SonCD on 11/09/2020
 */
public class FormattedDateMatcher {
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d{1,4}");

    public static boolean matchesDate(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }

    public static boolean matchesNumber(String number) {
        return NUMBER_PATTERN.matcher(number).matches();
    }


    public static long numDate(String date) {
        String[] sd = date.split("-");

        LocalDate localDateStartDate = LocalDate.of(Integer.parseInt(sd[0]), Integer.parseInt(sd[1]), Integer.parseInt(sd[2]));
        LocalDate localDateEndDate = LocalDate.now();
        return ChronoUnit.DAYS.between(localDateStartDate, localDateEndDate);
    }
}
