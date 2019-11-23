package ca.ubc.cs304.model;

import java.util.Date;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final int dlicense;
    private final Date fromDate;
    private final Date toDate;

    public ReservationModel(int confNo, String vtname, int dlicense, Date fromDate, Date toDate) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dlicense = dlicense;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getConfNo() {return confNo;}

    public String getVtname() {return vtname;}

    public int getDlicense() {return dlicense;}

    public Date getFromDate() {return fromDate;}

    public Date getToDate() {return toDate;}
}
