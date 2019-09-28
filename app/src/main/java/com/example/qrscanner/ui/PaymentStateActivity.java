package com.example.qrscanner.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrscanner.R;

public class PaymentStateActivity extends AppCompatActivity {

    private String payment;
    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_state);

        imageView = findViewById(R.id.imageState);
        textView = findViewById(R.id.textView_status);


        if (getIntent() != null){
                textView.setText("Успешно завершено");
                imageView.setImageResource(R.drawable.ic_checked);}
            else{
                textView.setText("Ошибка");
                imageView.setImageResource(R.drawable.ic_cancel);}

    }
}
