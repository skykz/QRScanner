package com.example.qrscanner.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.qrscanner.TestActivity;
import com.example.qrscanner.printer.PrinterActivity;
import com.example.qrscanner.scanner.ScannerActivity;
import com.example.qrscanner.ui.MainActivity;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashActivitySpec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Нет интернет соединении");
            builder.setMessage("Подключитесь к интернету...");

            builder.setNeutralButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                    System.exit(0);
                }
            });

            final AlertDialog alert1 = builder.create();
            alert1.show();

            final Button neutralButton = alert1.getButton(AlertDialog.BUTTON_NEUTRAL);
            LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) neutralButton.getLayoutParams();
            positiveButtonLL.gravity = Gravity.CENTER;
            neutralButton.setLayoutParams(positiveButtonLL);


            //Toast.makeText(getApplicationContext(),"Проблема с интернетом! Проверьте соединение с интернетом",Toast.LENGTH_LONG).show();
        }else {
                Intent intent = new Intent(this, PrinterActivity.class);
                startActivity(intent);
                finish();
//            } else {
//                Intent intent = new Intent(this, NumberActivity.class);
//                startActivity(intent);
//                finish();
//            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        getIntent();
        isOnline();
    }

}
