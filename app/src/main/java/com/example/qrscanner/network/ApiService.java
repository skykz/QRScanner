package com.example.qrscanner.network;

import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.Refund;
import com.example.qrscanner.model.ShiftIdRequest;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.UserPaymentResponse;
import com.example.qrscanner.model.UserPaymentsDepartment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;

public interface ApiService {

    // application requests, inner buttons
    @POST("getUserPayments")
    Call <List<UserPaymentResponse>> getUserPaymentsDepId(@Body UserPaymentsDepartment userPaymentsDepartment);

    @POST("refund")
    Call<String> doRefund (@Body Refund param);

    @POST("shifttransactions")
    Call<ShiftIdResponse> getShiftData (@Body ShiftIdRequest param);

    // requests from webView buttons
    @POST("deleteUserPayment")
    Call<String> doPayment (@Body Payment param);
}
