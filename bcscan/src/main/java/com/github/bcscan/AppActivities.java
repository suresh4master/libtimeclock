package com.github.bcscan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.bcscan.data.LibConstants;
import com.github.bcscan.data.LibUtils;
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

    private Context context;
    private DatabaseHandler dbHandler;

    public AppActivities(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inserts the supplied data into CoreData table
     *
     * @param coreData
     */
    public void insertCoreData(List<String[]> coreData) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        InsertCoreDataTask insertCoreDataTask = new InsertCoreDataTask(context, dbHandler, coreData);
        insertCoreDataTask.execute();
    }

    /**
     * Gets the coreDat for the supplied key
     *
     * @param key
     * @return
     */
    public DataObject getCoreData(String key) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getDataObject(key);
    }

    /**
     * @param data
     */
    public void insertScanDetails(String... data) {
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
            case 4:
                scannedData.setColumn1(data[0]);
                scannedData.setColumn2(data[1]);
                scannedData.setColumn3(data[2]);
                scannedData.setColumn3(data[3]);
                break;
            default:
        }
        dbHandler.addScannedData(scannedData);
    }

    /**
     * Returns the ScannedData for the specified date
     *
     * @param date
     * @return
     */
    public List<ScannedData> getScannedDataByDate(Date date) {
        if (null == date) {
            throw new RuntimeException("Please supply valid date");
        }
        dbHandler = DatabaseHandler.getDBInstance(context);
        return (null != date) ?
                dbHandler.getScannedDetails(LibUtils.getDateStr(date))
                : null;
    }

    /**
     * Returns the ScannedData for the specified dates
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ScannedData> getScannedDataByDateRange(Date fromDate, Date toDate) {
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
                dbHandler.getScannedDetails(LibUtils.getDateStr(fromDate), LibUtils.getDateStr(toDate))
                : null;
    }

    /**
     * Returns the complete Scanned data
     *
     * @return
     */
    public List<ScannedData> getAllScannedData() {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getScannedDetails();
    }

    /**
     * Inserts the supplied config into Config table
     *
     * @param key
     * @param value
     */
    public void insertConfig(String key, String value) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        dbHandler.updateConfig(key, value);
    }

    /**
     * Gets the configData for the supplied key
     *
     * @param key
     * @return
     */
    public String getConfigValue(String key) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getConfigValue(key);
    }

    /**
     * Get scanned data for the day
     *
     * @return
     */
    public Integer getTodayScannedCount() {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getDayScannedDataCount(LibConstants.getCurrentDateStr());
    }

    /**
     * Get scanned data count for the supplied date
     *
     * @param date
     * @return
     */
    public Integer getScannedCountForDay(Date date) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getDayScannedDataCount(LibUtils.getDateStr(date));
    }

    /**
     * Drop and create CoreData table
     */
    public void dropAndCreateCoreDataTable() {
        dbHandler = DatabaseHandler.getDBInstance(context);
        dbHandler.dropAndCreateCoreDataTable();
    }
}
