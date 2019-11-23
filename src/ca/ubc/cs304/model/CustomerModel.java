package ca.ubc.cs304.model;

public class CustomerModel {
    private final int dlicense;
    private final String cellphone;
    private final String name;
    private final String address;

    public CustomerModel(int dlicense, String cellphone, String name, String address) {
        this.dlicense = dlicense;
        this.cellphone = cellphone;
        this.name = name;
        this.address = address;
    }

    public int getDlicense() {return dlicense;}

    public String getCellphone() {return cellphone;}

    public String getName() {return name;}

    public String getAddress() {return address;}
}
