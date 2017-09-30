package com.github.bcscan.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SureshSri on 02/22/2017.
 */

public final class LibConstants {

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String CURRENT_DATE = "current_date";
    public static final String LOG_INFO = "Info-libbcscan";
    public static final String LOG_ERROR = "Error-libbcscan";

    public static DateFormat sdf;

    public static String getCurrentDateStr() {
        sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    public static String getCurrentTimeStr() {
        sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(new Date());
    }

}
