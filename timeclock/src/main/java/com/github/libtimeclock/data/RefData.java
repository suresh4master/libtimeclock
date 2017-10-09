package com.github.libtimeclock.data;

/**
 * Pojo for RefData
 */
public class RefData {

    private String refId;
    private String column2;
    private String column1;

    public RefData(String refId, String column1, String column2) {
        this.refId = refId;
        this.column1 = column1;
        this.column2 = column2;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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

}
