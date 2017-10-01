package com.github.libtimeclock.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "db";
    private static final String TABLE_CORE_DATA = "core_data";
    private static final String TABLE_SCANNED_DATA = "scanned_data";
    private static final String TABLE_CONFIG = "config";

    private static final String KEY_ID = "id";
    private static final String KEY_TIMESTAMP = "timestamp";

    private static final String KEY_COLUMN1 = "column1";
    private static final String KEY_COLUMN2 = "column2";
    private static final String KEY_COLUMN3 = "column3";
    private static final String KEY_COLUMN4 = "column4";

    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";

    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    private static final String CREATE_CORE_DATA_TABLE = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_CORE_DATA).append(" (")
            .append(KEY_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(KEY_COLUMN1).append(" TEXT UNIQUE, ")
            .append(KEY_COLUMN2).append(" TEXT, ")
            .append(KEY_COLUMN3).append(" TEXT )").toString();

    private static final String CREATE_SCANNED_DATA_TABLE = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_SCANNED_DATA).append(" (")
            .append(KEY_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
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
        dropTable(db, TABLE_CORE_DATA);
        createCoreDataTable(db);
        createScannedDataTable(db);
        createConfigTable(db);
        insertConfig("db_updated_date", "");
        insertConfig(LibConstants.CURRENT_DATE, LibConstants.getCurrentDateStr());
    }

    private void createCoreDataTable(final SQLiteDatabase db) {
        db.execSQL(CREATE_CORE_DATA_TABLE);
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
        dropTable(db, TABLE_CORE_DATA);
        createCoreDataTable(db);
    }

    public void addDataObjectsList(List<DataObject> dataObjects) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.w(LibConstants.LOG_INFO, "Start inserting into DB");
        db.beginTransaction();
        try {
            for (DataObject dataObject : dataObjects) {
                addDataObject(dataObject, db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.w(LibConstants.LOG_INFO, "End inserting into DB");
        db.close(); // Closing database connection
    }

    // Adding new DataObject
    private void addDataObject(DataObject dataObject, SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_COLUMN1, dataObject.getColumn1());
            values.put(KEY_COLUMN2, dataObject.getColumn2());
            values.put(KEY_COLUMN3, dataObject.getColumn3());
            db.insert(TABLE_CORE_DATA, null, values);
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Error inserting : " + dataObject.getColumn1() + "- " + ex.getMessage());
        }
    }

    // Getting single DataObject
    public DataObject getDataObject(String column1) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CORE_DATA, new String[]{KEY_COLUMN1,
                        KEY_COLUMN2, KEY_COLUMN3}, KEY_COLUMN1 + "=?",
                new String[]{String.valueOf(column1)}, null, null, null, null);
        DataObject dataObject = null;
        try {
            if (null != cursor) {
                cursor.moveToFirst();
                dataObject = new DataObject(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                cursor.close();
            }
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find dataObject: " + column1);
        }
        return dataObject;
    }

    // Getting Data Count
    public int getDataObjectsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CORE_DATA;
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

    public Integer getDayScannedDataCount(String currentDateStr) {
        try {
            String countQuery = "SELECT  * FROM " + TABLE_SCANNED_DATA + " WHERE " + KEY_DATE + "='" + currentDateStr + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();
            return cnt;
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find Scanned Details", ex);
            return 0;
        }
    }

    public List<ScannedData> getScannedDetails(String dateStr) {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_SCANNED_DATA, new String[]{KEY_COLUMN1,
                            KEY_COLUMN2, KEY_COLUMN3, KEY_COLUMN4, KEY_DATE, KEY_TIME}, KEY_DATE + "=?",
                    new String[]{String.valueOf(dateStr)}, null, null, null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                scannedDatas.add(new ScannedData(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4), c.getString(5)));
            }
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find ScannedData for date: " + dateStr, ex);
        }
        return scannedDatas;
    }

    public List<ScannedData> getScannedDetails(String fromDateStr, String toDateStr) {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.query(TABLE_SCANNED_DATA, new String[]{KEY_COLUMN1,
                            KEY_COLUMN2, KEY_COLUMN3, KEY_COLUMN4, KEY_DATE, KEY_TIME}, KEY_DATE + ">=? AND " + KEY_DATE + "=<?",
                    new String[]{fromDateStr, toDateStr}, null, null, null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                scannedDatas.add(new ScannedData(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4), c.getString(5)));
            }
        } catch (Exception ex) {
            Log.w(LibConstants.LOG_ERROR, "Couldn't find ScannedData for date: " + fromDateStr, ex);
        }
        return scannedDatas;
    }

    public List<ScannedData> getScannedDetails() {
        List<ScannedData> scannedDatas = new ArrayList<ScannedData>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCANNED_DATA, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                scannedDatas.add(new ScannedData(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
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