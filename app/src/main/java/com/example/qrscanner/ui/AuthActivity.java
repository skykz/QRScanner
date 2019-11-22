package com.example.qrscanner.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrscanner.R;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.Users;
import com.google.gson.Gson;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.qrscanner.network.RetrofitMain.getInstance;
import static com.example.qrscanner.utils.Constants.CASHIER_ID;
import static com.example.qrscanner.utils.Constants.LOGIN;
import static com.example.qrscanner.utils.Constants.MY_PREF;
import static com.example.qrscanner.utils.Constants.PASSWORD;
import static com.example.qrscanner.utils.Constants.SHIFT_ID;
import static com.example.qrscanner.utils.Constants.USER_DATA;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username,password;
    private Button btnLogin;
    private String mainPassword = "123456";// main password to exit
    final Context context = this;
    private ProgressDialog prDialog;
    public Users usersobj;
    private long mLastClickTime = 0;
    ShiftIdResponse shiftIdResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        SharedPreferences editor = getSharedPreferences(MY_PREF, MODE_PRIVATE);

        if (editor.contains(LOGIN) && editor.contains(PASSWORD) && editor.contains(USER_DATA)) {
            Intent  intent = new Intent(getBaseContext(), NavigationActivity.class);
            startActivity(intent);
        }
    }

    void validate(){
        if (username.getText().toString().equals("") && password.getText().toString().equals("")) {
            Toast.makeText(this, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show();
        }else if(username.getText().toString().equals("")){
            Toast.makeText(this, "Введите логин ", Toast.LENGTH_SHORT).show();
        }else if (password.getText().toString().equals("")){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }else {
            Users user = new Users(username.getText().toString(),password.getText().toString());
            authCashier(user);
        }
    }

    void authCashier(Users user){
        prDialog = ProgressDialog.show(AuthActivity.this, "Подождите", "Авторизация ...");
        prDialog.show();

        Call<Users> call = getInstance().getApiService().doAuth(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
              System.out.println(response.body());
                if (response.isSuccessful()) {
                    usersobj = response.body();
                    System.out.println(usersobj + "------------- AUTH");

                    Gson gson = new Gson();
                    String jsonData = gson.toJson(usersobj);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                    editor.putString(LOGIN, username.getText().toString());
                    editor.putString(PASSWORD, password.getText().toString());
                    editor.putInt(CASHIER_ID,Integer.parseInt(usersobj.cashierId));
                    editor.putString(USER_DATA,jsonData);

                    if (usersobj.shiftId != null)
                    editor.putInt(SHIFT_ID,Integer.parseInt(usersobj.shiftId));
                    else
                    System.out.println("SHIFT Id is NULL");

                    editor.commit();

                    Intent goNavigator = new Intent(AuthActivity.this,NavigationActivity.class);
                    startActivity(goNavigator);
                    AuthActivity.this.finish();
                    Toast.makeText(getBaseContext(),"Авторизация прошла успешна",Toast.LENGTH_SHORT).show();
                    prDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getBaseContext(),"логин или пароль неверный!",Toast.LENGTH_SHORT).show();
                Log.d("DONE !!!!! ошибка ", t.getMessage());
                prDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                validate();
//                ReportZOrX();
        break;}
    }



    // back button to exit, but user gets dialog popup to enter password
    @Override
    public void onBackPressed() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Назад",
                        (dialog, id) -> dialog.cancel())
                .setPositiveButton("Готово",
                        (dialog, id) -> {
                            String password = userInput.getText().toString();

                            if (password.isEmpty()) {
                                Toast.makeText(this, "Введите пароль!", Toast.LENGTH_SHORT).show();return;
                            }
                            if (password.equals(mainPassword)) {
                                finish();
                                System.exit(0);
                            }else
                            {
                                String message = "Пароль неверный!" + " \n\n" + "Попробуйте позже...";
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Ошибка");
                                builder.setMessage(message);
                                builder.setPositiveButton("Назад", null);
                                builder.create().show();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_password, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Назад",
                            (dialog, id) -> dialog.cancel())
                    .setPositiveButton("Готово",
                            (dialog, id) -> {
                                String password = userInput.getText().toString();

                                if (password.isEmpty()) {
                                    Toast.makeText(this, "Введите пароль!", Toast.LENGTH_SHORT).show();return;
                                }
                                if (password.equals(mainPassword)) {
                                    finish();
                                    System.exit(0);
                                }else
                                {
                                    String message = "Пароль неверный!" + " \n\n" + "Попробуйте позже...";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Ошибка");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Назад", null);
                                    builder.create().show();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.alert_password, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Назад",
                            (dialog, id) -> dialog.cancel())
                    .setPositiveButton("Готово",
                            (dialog, id) -> {
                                String password = userInput.getText().toString();

                                if (password.isEmpty()) {
                                    Toast.makeText(this, "Введите пароль!", Toast.LENGTH_SHORT).show();return;
                                }
                                if (password.equals(mainPassword)) {
                                    finish();
                                    System.exit(0);
                                }else
                                {
                                    String message = "Пароль неверный!" + " \n\n" + "Попробуйте позже...";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Ошибка");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Назад", null);
                                    builder.create().show();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        return false;
    }
}
