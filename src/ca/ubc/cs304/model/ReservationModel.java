package ca.ubc.cs304.model;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final int dlicense;
    private final String fromDate;
    private final String toDate;

    public ReservationModel(int confNo, String vtname, int dlicense, String fromDate, String toDate) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dlicense = dlicense;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getConfNo() {return confNo;}

    public String getVtname() {return vtname;}

    public int getDlicense() {return dlicense;}

    public String getFromDate() {return fromDate;}

    public String getToDate() {return toDate;}
}
