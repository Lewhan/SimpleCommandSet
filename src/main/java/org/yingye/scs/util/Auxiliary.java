package org.yingye.scs.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Auxiliary {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getFormatDate() {
        return getFormatDate(new Date(), DEFAULT_DATE_FORMAT);
    }

    public static String getFormatDate(Date date) {
        return getFormatDate(date, DEFAULT_DATE_FORMAT);
    }

    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static boolean isPositive(String str) {
        try {
            double d = Double.parseDouble(str);
            return d >= 0;
        }catch (Exception e) {
            return false;
        }
    }

    public static Optional<Number> isNumber(String str) {
        try {
            double d = Double.parseDouble(str);
            return Optional.of(d);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean notPositive(String str) {
        return !isPositive(str);
    }

}
