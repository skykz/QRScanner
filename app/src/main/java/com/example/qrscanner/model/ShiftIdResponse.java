package com.example.qrscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShiftIdResponse implements Serializable {
    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("Status")
    @Expose
    private String status;

    @SerializedName("StartDate")
    @Expose
    private String startDate;

    @SerializedName("EndDate")
    @Expose
    private String endDate;

    @SerializedName("Profit")
    @Expose
    private int profit;

    @SerializedName("DepartmentName")
    @Expose
    private String departmentName;

    @SerializedName("Transactions")
    @Expose
    private List<Transactions> transactions;

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return "ShiftIdResponse{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", profit=" + profit +
                ", departmentName='" + departmentName + '\'' +
                ", transactions=" + transactions +
                '}';
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
}
