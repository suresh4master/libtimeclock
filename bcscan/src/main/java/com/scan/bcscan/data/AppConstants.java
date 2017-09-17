package com.scan.bcscan.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SureshSri on 02/22/2017.
 */

public final class AppConstants {

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String CURRENT_DATE = "current_date";
    public static final String LOG_INFO = "Info-BCScanner";
    public static final String LOG_ERROR = "Error-BCScanner";
    public static final String IMEI_NUM = "IMEI_NUMBER";

    public static final String LOCAL_DB_FILE_NAME = "latest-reference.bz2";
    public static final String LOCAL_DB_DIR = "/Data/";
    public static final String DB_UPDATED_DATE = "db_updated_date";

    public static DateFormat sdf;

    public static String getCurrentDateStr() {
        sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    public static String getCurrentTimeStr() {
        sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(new Date());
    }

    public static String getCurrentTimeStamp() {
        sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdf.format(new Date());
    }
}
