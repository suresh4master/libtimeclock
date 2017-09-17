package com.scan.bcscan.data;

public class ScannedData {

    private String column1;
    private String column2;
    private String column3;
    private String date;
    private String time;

    public ScannedData(String column1, String column2, String column3) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
    }

    public ScannedData(String column1, String column2, String column3, String date, String time) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.date = date;
        this.time = time;
    }

    public ScannedData() {
        this.date = AppConstants.getCurrentDateStr();
        this.time = AppConstants.getCurrentTimeStr();
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
