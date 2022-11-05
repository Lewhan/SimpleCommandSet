package org.yingye.scs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Auxiliary {

    private static final String format = "yyyy-MM-dd HH:mm:ss";

    public static String getFormatDate() {
        return getFormatDate(new Date(), format);
    }

    public static String getFormatDate(Date date) {
        return getFormatDate(date, format);
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

    public static boolean notPositive(String str) {
        return !isPositive(str);
    }

}
