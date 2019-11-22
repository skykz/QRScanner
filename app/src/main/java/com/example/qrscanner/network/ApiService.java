package com.example.qrscanner.network;

import com.example.qrscanner.model.CashierPayment;
import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.Refund;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.StartShift;
import com.example.qrscanner.model.UserPaymentResponse;
import com.example.qrscanner.model.UserPaymentsDepartment;
import com.example.qrscanner.model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;

public interface ApiService {

    @POST("cashierauth")
    Call<Users> doAuth(@Body Users user);

    @POST("getUserPayments")
    Call <List<UserPaymentResponse>> getUserPaymentsDepId(@Body UserPaymentsDepartment userPaymentsDepartment);

    @POST("cashier-payment")
    Call<String> doPayment(@Body CashierPayment cashierPayment);

    @POST("startshift")
    Call<StartShift> openShift(@Body StartShift param);

    @POST("refund")
    Call<String> doRefund (@Body Refund param);

    // otchet X request
    @POST("shifttransactions")
    Call<ShiftIdResponse> getShiftData (@Body int shiftId);

    // otchet Z request
    @POST("cashierlogout")
    Call<ShiftIdResponse> doLogoutShift(@Body int shiftId);


    @POST("deleteUserPayment")
    Call<String> doDeleteUserPayment (@Body Payment param);
}
