package com.example.qrscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Users  implements Serializable {
    public String login;
    public String password;

    @SerializedName("CashierId")
    @Expose
    public String cashierId;

    @SerializedName("ShiftId")
    @Expose
    public String shiftId;

    @SerializedName("Gases")
    @Expose
    public List<Gases> gases;

    public Users(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public List<Gases> getGases() {
        return gases;
    }

    public void setGases(List<Gases> gases) {
        this.gases = gases;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "Users{" +
                "login ='" + login + '\'' +
                ", password ='" + password + '\'' +
                ", cashierId ='" + cashierId + '\'' +
                ", shiftId ='" + shiftId + '\'' +
                ", gases =" + gases +
                '}';
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
