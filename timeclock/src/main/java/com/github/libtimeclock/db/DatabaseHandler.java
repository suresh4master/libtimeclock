package com.github.libtimeclock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.libtimeclock.utilities.LibConstants;
import com.github.libtimeclock.utilities.LibUtils;
import com.github.libtimeclock.data.RefData;
import com.github.libtimeclock.data.ScannedData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "time_clock_db";
    private static final String TABLE_REF_DATA = "ref_data";
    private static final String TABLE_SCANNED_DATA = "scanned_data";
    private static final String TABLE_CONFIG = "config";

    private static final String KEY_ID = "id";
    private static final String KEY_TIMESTAMP = "timestamp";

    private static final String KEY_REF_ID = "ref_id";
    private static final String KEY_COLUMN1 = "column1";
    private static final String KEY_COLUMN2 = "column2";
    private static final String KEY_COLUMN3 = "column3";
    private static final String KEY_COLUMN4 = "column4";

    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";

    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    private static final String CREATE_REF_DATA_TABLE = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_REF_DATA).append(" (")
            .append(KEY_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(KEY_REF_ID).append(" TEXT UNIQUE, ")
            .append(KEY_COLUMN1).append(" TEXT, ")
            .append(KEY_COLUMN2).append(" TEXT )").toString();

    private static final String CREATE_SCANNED_DATA_TABLE = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_SCANNED_DATA).append(" (")
            .append(KEY_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(KEY_REF_ID).append(" TEXT, ")
            .append(KEY_COLUMN1).append(" TEXT, ")
            .append(KEY_COLUMN2).append(" TEXT, ")
            .append(KEY_COLUMN3).append(" TEXT, ")
            .append(KEY_COLUMN4).append(" TEXT, ")
            .append(KEY_DATE).append(" TEXT, ")
            .append(KEY_TIME).append(" TEXT )").toString();

    private static final String CREATE_CONFIG_TABLE = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_CONFIG).append(" (")
            .append(KEY_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(KEY_KEY).append(" TEXT UNIQUE, ")
            .append(KEY_VALUE).append(" TEXT, ")
            .append(KEY_TIMESTAMP).append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP)").toString();

    private static DatabaseHandler sInstance;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    public static DatabaseHandler getDBInstance(Context context) {
        if (null == sInstance) {
            sInstance = new DatabaseHandler(context);
        }
        return sInstance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Drop older table if existed
        dropTable(db, TABLE_REF_DATA);
        createRefDataTable(db);
        createScannedDataTable(db);
        createConfigTable(db);
        insertConfig(LibConstants.REF_DATA_UPDATED_DATE, "");
        insertConfig(LibConstants.CURRENT_DATE, LibConstants.getCurrentDateStr());
    }

    private void createRefDataTable(final SQLiteDatabase db) {
        db.execSQL(CREATE_REF_DATA_TABLE);
    }

    private void createScannedDataTable(SQLiteDatabase db) {
        db.execSQL(CREATE_SCANNED_DATA_TABLE);
    }

    private void createConfigTable(SQLiteDatabase db) {
        db.execSQL(CREATE_CONFIG_TABLE);
    }

    private void dropTable(final SQLiteDatabase db, final String table) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        onCreate(db);
    }

    public void dropAndCreateCoreDataTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropTable(db, TABLE_REF_DATA);
        createRefDataTable(db);
    }

    public void addRefDataList(List<RefData> refDatas) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.w(LibConstants.LOG_INFO, "Start inserting into DB");
        db.beginTransaction();
        try {
            for (RefData refData : refDatas) {
                addRefData(refData, db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.w(LibConstants.LOG_INFO, "End inserting into DB");
        db.close(); // Closing database connection
    }

    // Adding new RefData
    private void addRefData(RefData refData, SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_REF_ID, refData.getRefId());
            values.put(KEY_COLUMN1, refData.getColumn1());
            values.put(KEY_COLUMN2, refData.getColumn2());
            db.insert(TABLE_REF_DATA, null, values);
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Error inserting : " + refData.getRefId() + "- " + ex.getMessage());
        }
    }

    // Getting single RefData
    public RefData getRefData(String column1) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REF_DATA, new String[]{KEY_REF_ID,
                        KEY_COLUMN1, KEY_COLUMN2}, KEY_REF_ID + "=?",
                new String[]{String.valueOf(column1)}, null, null, null, null);
        RefData refData = null;
        try {
            if (null != cursor) {
                cursor.moveToFirst();
                refData = new RefData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                cursor.close();
            }
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find refData: " + column1);
        }
        return refData;
    }

    // Getting Ref Data Count
    public int getRefDataCount() {
        String countQuery = "SELECT * FROM " + TABLE_REF_DATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void addScannedData(ScannedData scannedData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_REF_ID, scannedData.getRefId());
            values.put(KEY_COLUMN1, scannedData.getColumn1());
            values.put(KEY_COLUMN2, scannedData.getColumn2());
            values.put(KEY_COLUMN3, scannedData.getColumn3());
            values.put(KEY_COLUMN4, scannedData.getColumn4());
            values.put(KEY_DATE, scannedData.getDate());
            values.put(KEY_TIME, scannedData.getTime());
            db.insert(TABLE_SCANNED_DATA, null, values);
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't scannedData", ex);
        }
    }

    public Integer getDayScannedDataCount(String dateStr) {
        try {
            String countQuery = "SELECT  * FROM " + TABLE_SCANNED_DATA + " WHERE " + KEY_DATE + "='" + dateStr + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();
            return cnt;
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find Scanned Data", ex);
            return 0;
        }
    }

    public Integer getDayScannedDataCount(String configKeyValue, String dateStr) {
        try {
            String countQuery = new StringBuilder("SELECT  * FROM ")
                    .append(TABLE_SCANNED_DATA)
                    .append(" WHERE ").append(KEY_DATE).append("='").append(dateStr).append("'")
                    .append(" AND ").append(KEY_COLUMN3).append("='").append(configKeyValue).append("'")
                    .toString();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();
            return cnt;
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find Scanned Data", ex);
            return 0;
        }
    }

    public List<ScannedData> getScannedDataByDate(String dateStr) {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        if (!LibUtils.isBlank(dateStr)) {
            try {
                SQLiteDatabase db = this.getReadableDatabase();

                Cursor c = db.query(TABLE_SCANNED_DATA, new String[]{
                                KEY_REF_ID,
                                KEY_COLUMN1, KEY_COLUMN2, KEY_COLUMN3,
                                KEY_COLUMN4, KEY_DATE, KEY_TIME},
                        KEY_DATE + "=?",
                        new String[]{String.valueOf(dateStr)}, null, null, null, null);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    scannedDatas.add(new ScannedData(
                            c.getString(0), c.getString(1),
                            c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6)));
                }
            } catch (Exception ex) {
                Log.w(LibConstants.LOG_ERROR, "Couldn't find ScannedData for date: " + dateStr, ex);
            }
        }
        return scannedDatas;
    }

    public List<ScannedData> getScannedDataByRefId(String refId) {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        if (!LibUtils.isBlank(refId)) {
            try {
                SQLiteDatabase db = this.getReadableDatabase();

                Cursor c = db.query(TABLE_SCANNED_DATA, new String[]{
                                KEY_REF_ID, KEY_COLUMN1, KEY_COLUMN2,
                                KEY_COLUMN3, KEY_COLUMN4, KEY_DATE, KEY_TIME},
                        KEY_REF_ID + "=?",
                        new String[]{String.valueOf(refId)}, null, null, null, null);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    scannedDatas.add(new ScannedData(
                            c.getString(0), c.getString(1),
                            c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6)));
                }
            } catch (Exception ex) {
                Log.w(LibConstants.LOG_ERROR, "Couldn't find ScannedData for the refId: " + refId, ex);
            }
        }
        return scannedDatas;
    }

    public List<ScannedData> getScannedDataByDateRange(String fromDateStr, String toDateStr) {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        if (!LibUtils.isBlank(fromDateStr) && !LibUtils.isBlank(toDateStr)) {
            try {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.query(TABLE_SCANNED_DATA, new String[]{
                                KEY_REF_ID, KEY_COLUMN1, KEY_COLUMN2,
                                KEY_COLUMN3, KEY_COLUMN4, KEY_DATE, KEY_TIME},
                        KEY_DATE + " BETWEEN ? AND ?",
                        new String[]{fromDateStr, toDateStr}, null, null, null, null);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    scannedDatas.add(new ScannedData(
                            c.getString(0), c.getString(1),
                            c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6)));
                }
            } catch (Exception ex) {
                Log.w(LibConstants.LOG_ERROR, "Couldn't find ScannedData for date: " + fromDateStr, ex);
            }
        }
        return scannedDatas;
    }

    public List<ScannedData> getAllScannedData() {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            //Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCANNED_DATA, null);
            Cursor cursor = db.query(TABLE_SCANNED_DATA, new String[]{
                            KEY_REF_ID, KEY_COLUMN1, KEY_COLUMN2,
                            KEY_COLUMN3, KEY_COLUMN4, KEY_DATE, KEY_TIME},
                            null, null, null, null, null, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                scannedDatas.add(new ScannedData(
                        cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            }
            cursor.close();
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't get ScannedData: ", ex);
        }
        return scannedDatas;
    }

    public String getConfigValue(String key) {
        String value = "";
        try {
            if (!LibUtils.isBlank(key)) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.query(TABLE_CONFIG, new String[]{KEY_VALUE}, KEY_KEY + "=?",
                        new String[]{key}, null, null, null, null);
                if (null != cursor) {
                    cursor.moveToFirst();
                    value = cursor.getString(0);
                    cursor.close();
                }
            }
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find config " + key, ex);
        }
        Log.w(LibConstants.LOG_INFO, "Getting config- " + key + ": " + value);
        return value;
    }

    public void insertConfig(String key, String value) {
        if (LibUtils.isBlank(getConfigValue(key))) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_KEY, key);
                values.put(KEY_VALUE, value);
                db.insert(TABLE_CONFIG, null, values);
            } catch (Exception ex) {
                Log.w(LibConstants.LOG_INFO, "Cannot insert config", ex);
            }
        } else {
            updateConfig(key, value);
        }
    }

    public void updateConfig(String key, String value) {
        if (!LibUtils.isBlank(getConfigValue(key))) {
            Log.w(LibConstants.LOG_INFO, "Updating config- " + key + ": " + value);
            try {
                ContentValues cv = new ContentValues();
                cv.put(KEY_VALUE, value);
                SQLiteDatabase db = this.getWritableDatabase();
                db.update(TABLE_CONFIG, cv, KEY_KEY + "='" + key + "'", null);
            } catch (Exception ex) {
                Log.w(LibConstants.LOG_INFO, "Cannot update config", ex);
            }
        } else {
            insertConfig(key, value);
        }
    }
}