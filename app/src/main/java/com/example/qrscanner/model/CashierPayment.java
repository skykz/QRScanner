package com.example.qrscanner.model;

public class CashierPayment {
    public int clientId;
    public int shiftId;
    public int price;
    public String accountName;
    public String gas;
    int priceLiter;

    public int getPriceLiter() {
        return priceLiter;
    }

    public void setPriceLiter(int priceLiter) {
        this.priceLiter = priceLiter;
    }

    public CashierPayment(int clientId, int shiftId, int price, String accountName, String gas,int liters) {
        this.clientId = clientId;
        this.shiftId = shiftId;
        this.price = price;
        this.accountName = accountName;
        this.gas = gas;
        this.priceLiter = liters;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }
}
