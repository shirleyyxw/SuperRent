package ca.ubc.cs304.model;

public class ReturnModel {
    private final int rid;
    private final String date;
    private final int odometer;
    private final boolean fulltank;
    private final float value;

    public ReturnModel(int rid, String date, int odometer, boolean fulltank, float value) {
        this.rid = rid;
        this.date = date;
        this.odometer = odometer;
        this.fulltank = fulltank;
        this.value = value;
    }

    public int getRid() {
        return rid;
    }

    public String getDate() {
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
