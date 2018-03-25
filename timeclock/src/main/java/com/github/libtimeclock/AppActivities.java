package com.github.libtimeclock;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.libtimeclock.data.RefData;
import com.github.libtimeclock.db.DatabaseHandler;
import com.github.libtimeclock.utilities.LibConstants;
import com.github.libtimeclock.utilities.LibUtils;
import com.github.libtimeclock.data.ScannedData;

import java.util.Date;
import java.util.List;

/**
 * Activities that can be done by the library
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
     * Inserts the supplied data into RefData
     *
     * @param refDatas
     */
    public void addRefData(List<RefData> refDatas) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        dbHandler.addRefDataList(refDatas);
    }

    /**
     * Gets the RefData for the supplied refId
     *
     * @param refId
     * @return
     */
    public RefData getRefData(String refId) {
        validateStr(refId);
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getRefData(refId);
    }

    /**
     * Records the refId information as ScannedData
     *
     * @param refId     - RefId for the Scanned data
     * @param configKey - ConfigKey where additional info
     * @return
     */
    public ScannedData clockId(String refId, String configKey) {
        validateStr(refId);
        dbHandler = DatabaseHandler.getDBInstance(context);
        RefData refData = dbHandler.getRefData(refId);
        ScannedData scannedData = new ScannedData();
        scannedData.setRefId(refId);
        if (!LibUtils.isBlank(configKey)) {
            scannedData.setColumn3(dbHandler.getConfigValue(configKey));
        }
        if (null != refData) {
            scannedData.setColumn1(refData.getColumn1());
            scannedData.setColumn2(refData.getColumn2());
        }
        dbHandler.addScannedData(scannedData);
        return scannedData;
    }

    /**
     * Records the refId information as ScannedData
     *
     * @param refId
     * @param configKey
     * @param additionalInfo
     * @return
     */
    public ScannedData clockId(String refId, String configKey, String additionalInfo) {
        validateStr(refId);
        dbHandler = DatabaseHandler.getDBInstance(context);
        RefData refData = dbHandler.getRefData(refId);
        ScannedData scannedData = new ScannedData();
        scannedData.setRefId(refId);
        scannedData.setColumn4(additionalInfo);
        if (!LibUtils.isBlank(configKey)) {
            scannedData.setColumn3(dbHandler.getConfigValue(configKey));
        }
        if (null != refData) {
            scannedData.setColumn1(refData.getColumn1());
            scannedData.setColumn2(refData.getColumn2());
        }
        dbHandler.addScannedData(scannedData);
        return scannedData;
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
                dbHandler.getScannedDataByDate(LibUtils.getDateStr(date))
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
                dbHandler.getScannedDataByDateRange(LibUtils.getDateStr(fromDate), LibUtils.getDateStr(toDate))
                : null;
    }

    /**
     * Returns the complete Scanned data
     *
     * @return
     */
    public List<ScannedData> getAllScannedData() {
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getAllScannedData();
    }

    /**
     * Get scanned data for the supplied refId
     *
     * @param refId
     * @return
     */
    public List<ScannedData> getScannedDataById(String refId) {
        validateStr(refId);
        dbHandler = DatabaseHandler.getDBInstance(context);
        return dbHandler.getScannedDataByRefId(refId);
    }

    /**
     * Inserts the supplied config into Config
     *
     * @param key
     * @param value
     */
    public void insertConfig(String key, String value) {
        validateStr(key);
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
        validateStr(key);
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
     * Get scanned data for the supplied Date and the configKey
     *
     * @return
     */
    public Integer getScannedCountByConfigKeyAndDate(String configKey, Date date) {
        dbHandler = DatabaseHandler.getDBInstance(context);
        validateStr(configKey);
        return dbHandler.getDayScannedDataCount(dbHandler.getConfigValue(configKey), LibUtils.getDateStr(date));
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
     * Resets the RefData
     */
    public void resetRefData() {
        dbHandler = DatabaseHandler.getDBInstance(context);
        dbHandler.dropAndCreateCoreDataTable();
    }

    private void validateStr(String str) {
        if (LibUtils.isBlank(str)) {
            throw new RuntimeException("Supplied data is not valid: " + str);
        }
    }
}
