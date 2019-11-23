package ca.ubc.cs304.model;

import java.util.Date;

public class RentalModel {
    private final int rid;
    private final String vlicense;
    private final String dlicense;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final Date expDate;
    private final Date fromDate;
    private final Date toDate;
    private final int confNo;

    public RentalModel(int rid, String vlicense, String dlicense, int odometer, String cardName,
                       int cardNo, Date expDate, Date fromDate, Date toDate, int confNo) {
        this.rid = rid;
        this.vlicense = vlicense;
        this.dlicense = dlicense;
        this.odometer = odometer;
        this.cardName = cardName;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.confNo = confNo;
    }

    public int getRid() {
        return rid;
    }

    public String getVlicense() {
        return vlicense;
    }

    public String getDlicense() {
        return dlicense;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardNo() {
        return cardNo;
    }

    public Date getExpDate() {
        return expDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public int getConfNo() {
        return confNo;
    }
}
