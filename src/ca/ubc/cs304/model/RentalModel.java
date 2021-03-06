package ca.ubc.cs304.model;

public class RentalModel {
    private final int rid;
    private final String vlicense;
    private final int dlicense;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final String expDate;
    private final String fromDate;
    private final String toDate;
    private final int confNo;

    public RentalModel(int rid, String vlicense, int dlicense, int odometer, String cardName,
                       int cardNo, String expDate, String fromDate, String toDate, int confNo) {
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

    public int getDlicense() {
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

    public String getExpDate() {
        return expDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public int getConfNo() {
        return confNo;
    }
}
