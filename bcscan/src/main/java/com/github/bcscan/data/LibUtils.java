package com.github.bcscan.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LibUtils {
    private static final String EMPTY_STR = "";

    public static boolean isBlank(String str) {
        return (null == str || EMPTY_STR.equalsIgnoreCase(str.trim()));
    }

    public static void showAlertDialog(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String getDateStr(Date date) {
        return (null != date) ?
                new SimpleDateFormat(LibConstants.DATE_FORMAT)
                        .format(date) : null;
    }

}
