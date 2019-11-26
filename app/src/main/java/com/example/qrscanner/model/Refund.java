package com.example.qrscanner.model;

public class Refund {

    private String login;
    private long cash;
    private String account;
    private int idTransaction;

    public Refund(String clientId,int idTransaction, long sum,String account) {
        this.login = clientId;
        this.idTransaction = idTransaction;
        this.cash = sum;
        this.account = account;
    }
}
