package com.example.qrscanner.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.qrscanner.R;

public class PaymentStateActivity extends AppCompatActivity {

    private String payment;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_state);

        imageView = findViewById(R.id.imageState);

        if (payment == null)
            imageView.setImageResource(R.drawable.your_logo);
        else
            imageView.setImageResource(R.drawable.splash_back);

    }
}
