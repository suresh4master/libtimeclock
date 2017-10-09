package com.github.libtimeclock.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class LibConstants {

    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String CURRENT_DATE = "current_date";
    public static final String LOG_INFO = "Info-libtimeclock";
    public static final String LOG_ERROR = "Error-libtimeclock";
    public static final String REF_DATA_UPDATED_DATE = "ref_data_updated_date";

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
