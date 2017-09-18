package com.github.bcscan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.bcscan.data.AppConstants;
import com.github.bcscan.data.AppUtils;
import com.github.bcscan.data.DataObject;
import com.github.bcscan.data.DatabaseHandler;
import com.github.bcscan.data.InsertCoreDataTask;
import com.github.bcscan.data.ScannedData;

import java.util.Date;
import java.util.List;


/**
 * Created by SureshSri on 08/28/2017.
 */

public class AppActivities extends AppCompatActivity {

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inserts the supplied data into CoreData table
     *
     * @param context
     * @param coreData
     */
    public void insertCoreData(Context context, List<String[]> coreData) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        InsertCoreDataTask insertCoreDataTask = new InsertCoreDataTask(context, dbHandler, coreData);
        insertCoreDataTask.execute();
    }

    /**
     * Gets the coreDat for the supplied key
     *
     * @param context
     * @param key
     * @return
     */
    public DataObject getCoreData(Context context, String key) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getDataObject(key);
    }

    /**
     * @param context
     * @param data
     */
    public void insertScanDetails(Context context, String... data) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        ScannedData scannedData = new ScannedData();
        switch (data.length) {
            case 1:
                scannedData.setColumn1(data[0]);
                break;
            case 2:
                scannedData.setColumn1(data[0]);
                scannedData.setColumn2(data[1]);
                break;
            case 3:
                scannedData.setColumn1(data[0]);
                scannedData.setColumn2(data[1]);
                scannedData.setColumn3(data[2]);
                break;
            default:
        }
        scannedData.setDate(AppConstants.getCurrentDateStr());
        scannedData.setTime(AppConstants.getCurrentTimeStr());
        dbHandler.addScannedData(scannedData);
    }

    /**
     * Returns the ScannedData for the specified date
     *
     * @param context
     * @param date
     * @return
     */
    public List<ScannedData> getScannedDataByDate(Context context, Date date) {
        if (null == date) {
            throw new RuntimeException("Please supply valid date");
        }
        dbHandler = DatabaseHandler.getDBInstance(context);
        return (null != date) ?
                dbHandler.getScannedDetails(AppUtils.getDateStr(date))
                : null;
    }

    /**
     * Returns the ScannedData for the specified dates
     *
     * @param context
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ScannedData> getScannedDataByDateRange(Context context, Date fromDate, Date toDate) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        Date temp = fromDate;
        if ((null == fromDate) && (null == toDate)) {
            throw new RuntimeException("Please supply valid dates");
        }
        if (fromDate.after(toDate)) {
            fromDate = toDate;
            toDate = temp;
        }
        return (null != fromDate) && (null != toDate) ?
                dbHandler.getScannedDetails(AppUtils.getDateStr(fromDate), AppUtils.getDateStr(toDate))
                : null;
    }

    /**
     * Returns the complete Scanned data
     *
     * @param context
     * @return
     */
    public List<ScannedData> getAllScannedData(Context context) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getScannedDetails();
    }

    /**
     * Inserts the supplied config into Config table
     *
     * @param context
     * @param key
     * @param value
     */
    public void InsertConfig(Context context, String key, String value) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        dbHandler.updateConfig(key, value);
    }

    /**
     * Gets the configData for the supplied key
     *
     * @param context
     * @param key
     * @return
     */
    public String getConfigValue(Context context, String key) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getConfigValue(key);
    }
}
