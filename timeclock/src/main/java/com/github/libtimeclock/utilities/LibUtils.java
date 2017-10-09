package com.github.libtimeclock.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LibUtils {
    private static final String EMPTY_STR = "";

    public static boolean isBlank(String str) {
        return (null == str || EMPTY_STR.equalsIgnoreCase(str.trim()));
    }

    public static String getDateStr(Date date) {
        return (null != date) ?
                new SimpleDateFormat(LibConstants.DATE_FORMAT)
                        .format(date) : null;
    }

}
