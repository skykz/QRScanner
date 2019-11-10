package com.example.qrscanner.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.qrscanner.R;
import com.example.qrscanner.ui.NavigationActivity;


public class SplashScreen extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
            builder.setTitle("Нет интернет соединении");
            builder.setMessage("Попробуйте позже...");
            builder.setIcon(R.drawable.ic_no_network);
            builder.setCancelable(false);

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

            Toast.makeText(getApplicationContext(),"Проблема с интернетом! Проверьте соединение с интернетом",Toast.LENGTH_LONG).show();
        }else {
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
                finish();
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
        isOnline();
    }

}
