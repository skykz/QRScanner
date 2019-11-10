package com.example.qrscanner.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transactions {
    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("Price")
    @Expose
    private int price;

    @SerializedName("Balance")
    @Expose
    private String balance;

    @SerializedName("Gas")
    @Expose
    private String gas;

    @SerializedName("Login")
    @Expose
    private String login;

    @SerializedName("Liters")
    @Expose
    private int liters;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getLiters() {
        return liters;
    }

    public void setLiters(int liters) {
        this.liters = liters;
    }
}