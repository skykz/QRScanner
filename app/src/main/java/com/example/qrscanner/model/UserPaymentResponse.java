package com.example.qrscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPaymentResponse {

    @SerializedName("paymentId")
    @Expose
    public int paymentId;

    @SerializedName("balance")
    @Expose
    public String balance;

    @SerializedName("login")
    @Expose
    public String login;
    //
    @SerializedName("personId")
    @Expose
    public int personId;
    //
    @SerializedName("departmentId")
    @Expose
    public int departmentId;

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }


}
