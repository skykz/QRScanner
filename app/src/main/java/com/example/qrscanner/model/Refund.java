package com.example.qrscanner.model;

public class Refund {

    private String login;
    private long cash;
    private String account;

    public Refund(String clientId, long sum,String account) {
        this.login = clientId;
        this.cash = sum;
        this.account = account;
    }
}
