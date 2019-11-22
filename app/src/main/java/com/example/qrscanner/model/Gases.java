package com.example.qrscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Gases implements Serializable {

    @SerializedName("Id")
    @Expose
    public int gasId;

    @SerializedName("Name")
    @Expose
    public String gasName;

    @SerializedName("Price")
    @Expose
    public int gasPrice;

    public int getGasId() {
        return gasId;
    }

    public void setGasId(int gasId) {
        this.gasId = gasId;
    }

    public String getGasName() {
        return gasName;
    }

    public void setGasName(String gasName) {
        this.gasName = gasName;
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public String toString() {
        return "Gases { " +
                "gasId = " + gasId +
                ", gasName = '" + gasName + '\'' +
                ", gasPrice = " + gasPrice +
                '}';
    }
}
