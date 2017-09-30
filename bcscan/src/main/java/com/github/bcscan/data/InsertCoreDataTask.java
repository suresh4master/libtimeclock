package com.github.bcscan.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SureshSri on 02/08/2017.
 */

public class InsertCoreDataTask extends AsyncTask<String, Void, String> {

    private Context context;
    private DatabaseHandler dbHandler;
    private Handler handler;
    private ProgressDialog progressDialog;
    private List<String[]> coreDataList;

    public InsertCoreDataTask(Context context, DatabaseHandler dbHandler, List<String[]> coreDataList) {
        this.context = context;
        this.dbHandler = dbHandler;
        this.coreDataList = coreDataList;
        handler = new Handler(context.getMainLooper());
    }

    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Updating data", "Please wait...");
    }

    @Override
    public String doInBackground(String... params) {
        insertCoreData(coreDataList);
        Log.w(LibConstants.LOG_INFO, "Start Reading CSV file ");
        return "Success";
    }

    private void insertCoreData(List<String[]> coreDataList) {
        int i = 0;
        List<DataObject> dataObjectsList = new ArrayList<DataObject>();
        int totalSize = 0;
        DataObject datObject;

        for (String[] coreData : coreDataList) {
            i++;
            totalSize++;
            datObject = getDataObject(coreData);
            if (null != datObject) {
                dataObjectsList.add(datObject);
            }
            if (i == 5000) {
                i = 0;
                dbHandler.addDataObjectsList(dataObjectsList);
                Log.w(LibConstants.LOG_INFO, "Completed inserting: " + totalSize);
                dataObjectsList = new ArrayList<DataObject>();
            }
        }
        if (i > 0) {
            dbHandler.addDataObjectsList(dataObjectsList);
            Log.w(LibConstants.LOG_INFO, "Completed inserting: " + totalSize);
            dataObjectsList = new ArrayList<DataObject>();
        }

    }

    public static DataObject getDataObject(final String[] data) {
        DataObject dataObject = null;
        if (null != data && 3 <= data.length) {
            dataObject = new DataObject(getCSVString(data[0]), getCSVString(data[1]), getCSVString(data[2]));
        }
        return dataObject;
    }

    private static String getCSVString(final String str) {
        return str.replaceAll("^\"|\"$", "");
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        showToastMsg("Successfully updated database");
        handler.post(new Runnable() {
            public void run() {
                LibUtils.showAlertDialog(context, "Success", "Updated successfully");
            }
        });
    }

    private void showToastMsg(final String msg) {
        handler.post(new Runnable() {
            public void run() {
                Log.w(LibConstants.LOG_INFO, msg);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
