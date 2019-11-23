package ca.ubc.cs304.model;

import java.util.Date;

public class ReturnModel {
    private final int rid;
    private final Date date;
    private final int odometer;
    private final boolean fulltank;
    private final float value;

    public ReturnModel(int rid, Date date, int odometer, boolean fulltank, float value) {
        this.rid = rid;
        this.date = date;
        this.odometer = odometer;
        this.fulltank = fulltank;
        this.value = value;
    }

    public int getRid() {
        return rid;
    }

    public Date getDate() {
        return date;
    }

    public int getOdometer() {
        return odometer;
    }

    public boolean getFulltank() {
        return fulltank;
    }

    public float getValue() {
        return value;
    }
}
