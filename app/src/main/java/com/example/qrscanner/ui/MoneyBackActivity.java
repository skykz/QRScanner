package com.example.qrscanner.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.R;
import com.example.qrscanner.model.Refund;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.qrscanner.network.RetrofitMain.getInstance;
import static com.example.qrscanner.utils.Constants.ACCOUNT;

public class MoneyBackActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView sum;
    private EditText clientId;
    private Intent intent;
    private Button one, two, three, four, five, six, seven, eight, nine, zero ,removeDigit,clear,btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_back);

         toolbar = (Toolbar) findViewById(R.id.toolbar_money);


        sum = (TextView)findViewById(R.id.textSum);
        clientId = (EditText)findViewById(R.id.inputClientId);
        clear = (Button)findViewById(R.id.buttonClearText);
        removeDigit = (Button)findViewById(R.id.buttonRemove);
        btnReturn = (Button)findViewById(R.id.button_return);
        one = (Button)findViewById(R.id.button1);
        two = (Button)findViewById(R.id.button2);
        three = (Button)findViewById(R.id.button3);
        four = (Button)findViewById(R.id.button4);
        five = (Button)findViewById(R.id.button5);
        six = (Button)findViewById(R.id.button6);
        seven = (Button)findViewById(R.id.button7);
        eight = (Button)findViewById(R.id.button8);
        nine = (Button)findViewById(R.id.button9);
        zero = (Button)findViewById(R.id.buttonZero);

        toolbar.setOnClickListener(this);
        clear.setOnClickListener(this);
        removeDigit.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.toolbar_money:
                onBackPressed();
                break;

            case R.id.button_return:

             if (!sum.getText().toString().isEmpty() && !clientId.getText().toString().isEmpty()) {

                 long cash = Long.parseLong(sum.getText().toString());
                 String login = clientId.getText().toString();
                 Refund refund = new Refund(login,cash,ACCOUNT);

                 doRefundPayment(refund);
             }  else
                 {
                     Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_SHORT).show();
                 }
            break;
            case R.id.button1:
                sum.setText(String.format("%s1", sum.getText()));
                break;
            case R.id.button2:
                sum.setText(String.format("%s2", sum.getText()));
                break;
            case R.id.button3:
                sum.setText(String.format("%s3", sum.getText()));
                break;
            case R.id.button4:
                sum.setText(String.format("%s4", sum.getText()));
                break;
            case R.id.button5:
                sum.setText(String.format("%s5", sum.getText()));
                break;
            case R.id.button6:
                sum.setText(String.format("%s6", sum.getText()));
                break;
            case R.id.button7:
                sum.setText(String.format("%s7", sum.getText()));
                break;
            case R.id.button8:
                sum.setText(String.format("%s8", sum.getText()));
                break;
            case R.id.buttonZero:
                sum.setText(String.format("%s0", sum.getText()));
                break;
            case R.id.buttonClearText:
                if (sum.getText() != null)
                    sum.setText(null);
                break;
            case R.id.buttonRemove:
                if (sum.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),"Поля пуста",Toast.LENGTH_SHORT).show();
                }else {
                    String s = sum.getText().toString();
                    s = s.substring(0, s.length() - 1);
                    sum.setText(s);
                }                break;
            case R.id.button9:
                sum.setText(String.format("%s9", sum.getText()));
                break;
        }
    }

    void doRefundPayment(Refund refund){
        Call<String> call = getInstance().getApiService().doRefund(refund);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        System.out.println(response.body());
                        if (response.body().contains("true"))
                        Toast.makeText(getBaseContext(),"Возврат прошёл успешно!",Toast.LENGTH_SHORT).show();
                        else
                        Toast.makeText(getBaseContext(),"Возрат не осуществлён!",Toast.LENGTH_SHORT).show();

                        intent = new Intent(MoneyBackActivity.this,NavigationActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_LONG).show();
                    Log.d("DONE !!!!! POST ", "Refund is bad" + response.message());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER ошибка"+t.getMessage(),Toast.LENGTH_LONG).show();
                Log.d("DONE !!!!! POST ", "SERVER error");
            }
        });
    }
}

