package com.example.qrscanner.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.qrscanner.R;
import com.example.qrscanner.model.CashierPayment;
import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.StartShift;
import com.example.qrscanner.model.Users;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.qrscanner.network.RetrofitMain.getInstance;
import static com.example.qrscanner.utils.Constants.ACCOUNT;
import static com.example.qrscanner.utils.Constants.CASHIER_ID;
import static com.example.qrscanner.utils.Constants.LOGIN;
import static com.example.qrscanner.utils.Constants.MY_PREF;
import static com.example.qrscanner.utils.Constants.PASSWORD;
import static com.example.qrscanner.utils.Constants.PAYMENT_ID;
import static com.example.qrscanner.utils.Constants.PERSON_ID;
import static com.example.qrscanner.utils.Constants.SHIFT_ID;
import static com.example.qrscanner.utils.Constants.USER_DATA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private ProgressDialog prDialog;
    int paymentID,personID,shiftId;
    private Toolbar toolbar;
    private TextView sum,personText;
    private Intent intent;
    private Button one, two, three, four, five, six, seven, eight, nine, zero ,removeDigit,clear,btnPayment,btnStartShift;
    private Button btnXOtchet,btnZOtchet,btnLogout;
    String [] gasList;
    String[] voluteList = {"Тенге", "Литры"};
    Users users;
    String oilType = "";
    String LiterOrMoney = "";
    Map<String,Integer> priceList = new HashMap<>();
    ShiftIdResponse shiftIdResponse;
    private Gson gson;
    private SharedPreferences preferences;
    private long mLastClickTime = 0;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    //TODO: commit on a new branch
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        Spinner gas = (Spinner) findViewById(R.id.spinner_gas);
        Spinner volute = (Spinner) findViewById(R.id.spinner_money);

        sum = (TextView) findViewById(R.id.textSum);
        personText = (TextView) findViewById(R.id.txtPersonId);
        clear = (Button) findViewById(R.id.buttonClearText);
        removeDigit = (Button) findViewById(R.id.buttonRemove);
        one = (Button) findViewById(R.id.button1);
        two = (Button) findViewById(R.id.button2);
        three = (Button) findViewById(R.id.button3);
        four = (Button) findViewById(R.id.button4);
        five = (Button) findViewById(R.id.button5);
        six = (Button) findViewById(R.id.button6);
        seven = (Button) findViewById(R.id.button7);
        eight = (Button) findViewById(R.id.button8);
        nine = (Button) findViewById(R.id.button9);
        zero = (Button) findViewById(R.id.buttonZero);
        btnPayment = (Button) findViewById(R.id.button_payment);
        btnStartShift = (Button) findViewById(R.id.btnStartShift);
        btnZOtchet = (Button) findViewById(R.id.btnZ);
        btnXOtchet = (Button) findViewById(R.id.btnX);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        //actions onClick
        clear.setOnClickListener(this);
        removeDigit.setOnClickListener(this);
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
        toolbar.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        btnStartShift.setOnClickListener(this);
        btnZOtchet.setOnClickListener(this);
        btnXOtchet.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnPayment.setEnabled(false);
        autoConnection();

        gson = new Gson();
        preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String json = preferences.getString(USER_DATA, null);

        shiftId = preferences.getInt(SHIFT_ID, 0);
        paymentID = preferences.getInt(PAYMENT_ID, 0);
        personID = preferences.getInt(PERSON_ID, 0);
        users = gson.fromJson(json, Users.class);

        if (shiftId != 0){
            btnZOtchet.setVisibility(View.VISIBLE);
            btnXOtchet.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnStartShift.setVisibility(View.GONE);
            btnPayment.setEnabled(true);
        }else{
            btnZOtchet.setVisibility(View.GONE);
            btnXOtchet.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnStartShift.setVisibility(View.VISIBLE);
            btnPayment.setEnabled(false);
        }


        if (users != null) {
            gasList = new String[users.gases.size()];
            System.out.println(" --------------- " + users.getGases() + "\n");
            personText.setText(String.valueOf(personID));

            for (int i = 0; i < users.getGases().size(); i++) {
                gasList[i] = users.gases.get(i).gasName;
                priceList.put(users.gases.get(i).gasName, users.gases.get(i).gasPrice);
            }
        }
            ArrayAdapter<String> adapterVolute = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, voluteList);
            adapterVolute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            volute.setAdapter(adapterVolute);

            ArrayAdapter<String> adapterGas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gasList);
            adapterGas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gas.setAdapter(adapterGas);

            volute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextSize(11);
                    LiterOrMoney = (String) parent.getItemAtPosition(position);
                    System.out.println("&&&&&&&&&&&&& " + LiterOrMoney);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    oilType = (String) parent.getItemAtPosition(position);
                    ((TextView) parent.getChildAt(0)).setTextSize(12);

                    System.out.println("###################### " + oilType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(getBaseContext(), "Выберите вид Бензин", Toast.LENGTH_LONG).show();
                }
            };
            gas.setOnItemSelectedListener(itemSelectedListener);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (shiftId != 0){
            btnZOtchet.setVisibility(View.VISIBLE);
            btnXOtchet.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnStartShift.setVisibility(View.GONE);
            btnPayment.setEnabled(true);
        }else{
            btnZOtchet.setVisibility(View.GONE);
            btnXOtchet.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnStartShift.setVisibility(View.VISIBLE);
            btnPayment.setEnabled(false);
        }
    }

    private void goToMain(){
        Intent mainIntent = new Intent(MainActivity.this, NavigationActivity.class);
        startActivity(mainIntent);
        finish();
//        prDialog = ProgressDialog.show(this, "Подождите", "Загружаем ...");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 2500);
//        prDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_main:
                onBackPressed();
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
                }
                break;
            case R.id.button9:
                sum.setText(String.format("%s9", sum.getText()));
                break;
            case R.id.btnStartShift:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                btnZOtchet.setVisibility(View.VISIBLE);
                btnXOtchet.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.VISIBLE);
                btnStartShift.setVisibility(View.GONE);
                btnPayment.setEnabled(true);
                System.out.println(users.cashierId + " ========= ");

                StartShift startShift =  new StartShift(Integer.valueOf(users.cashierId));
                openShift(startShift);

                break;
            case R.id.button_payment:
                System.out.println(oilType + " ------------");
                calculateResult(oilType,LiterOrMoney);
                break;
            case R.id.btnLogout:
                System.out.println("--------------- logout ");
                logout();
                break;
            case R.id.btnX:
                int localShiftId = preferences.getInt(SHIFT_ID, 0);
                reportX("X - Отчет",localShiftId);
                break;
            case R.id.btnZ:
                localShiftId = preferences.getInt(SHIFT_ID, 0);
                reportZ("Z - Отчет",localShiftId);
                break;
        }
    }

    void reportX(String titleReport,int shiftID){
        prDialog = ProgressDialog.show(this, "Подождите", "Загружаем ...");

        Call<ShiftIdResponse> call = getInstance().getApiService().getShiftData(shiftID);
        call.enqueue(new Callback <ShiftIdResponse>() {
            @Override
            public void onResponse(Call<ShiftIdResponse> call, Response<ShiftIdResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        ShiftIdResponse shiftIdResponse = response.body();
                        intent = new Intent(getBaseContext(), ReportDisplayActivity.class);
                        intent.putExtra("reportData",shiftIdResponse);
                        intent.putExtra("title",titleReport);
                        startActivity(intent);
                    }
                    prDialog.dismiss();
                } else {
                    Toast.makeText(getBaseContext(),"Нет данных",Toast.LENGTH_SHORT).show();
                    Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
                    prDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ShiftIdResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER error",Toast.LENGTH_SHORT).show();
                Log.d("OnFailure ---- ERROR ", t.getMessage());
                prDialog.dismiss();
            }
        });
    }

    void reportZ(String titleReport,int shiftID){
        prDialog = ProgressDialog.show(this, "Подождите", "Загружаем ...");

        Call<ShiftIdResponse> call = getInstance().getApiService().doLogoutShift(shiftID);
        call.enqueue(new Callback <ShiftIdResponse>() {
            @Override
            public void onResponse(Call<ShiftIdResponse> call, Response<ShiftIdResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        System.out.println(" ---- ------- " + response.body() + "\n\n");
                        if (response.body().getTransactions().size() != 0) {
                            shiftIdResponse = response.body();
                            preferences.edit().remove(SHIFT_ID).commit();
                            int shiftID = preferences.getInt(SHIFT_ID, 0);//"No name defined" is the default value.
                            System.out.println(shiftID + " ---- shift id is removed! ");
                            try {
                                printData(shiftIdResponse);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getBaseContext(), "Ошибка во время распечатки", Toast.LENGTH_SHORT).show();
                            }
                            intent = new Intent(getBaseContext(), ReportDisplayActivity.class);
                            intent.putExtra("reportData", shiftIdResponse);
                            intent.putExtra("title", titleReport);
                            startActivity(intent);
                        }
                    }
                    prDialog.dismiss();
                } else {
                    Toast.makeText(getBaseContext(),"Ответ у нас пустой",Toast.LENGTH_SHORT).show();
                    Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
                    prDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ShiftIdResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER error",Toast.LENGTH_SHORT).show();
                Log.d("OnFailure ---- ERROR ", t.getMessage());
                prDialog.dismiss();
            }
        });
    }


    void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Важно")
                .setMessage("Вы уверены выйти?")
                .setIcon(R.drawable.ic_cancel)
                .setCancelable(true)
                .setPositiveButton("Ок, Выйти",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                preferences.edit().remove(LOGIN).commit();
                                preferences.edit().remove(PASSWORD).commit();
                                preferences.edit().remove(PERSON_ID).commit();
                                preferences.edit().remove(PAYMENT_ID).commit();
                                preferences.edit().remove(USER_DATA).commit();
                                preferences.edit().remove(CASHIER_ID).commit();
                                String login = preferences.getString(LOGIN, "----- NO Login \n");//"No name defined" is the default value.
                                System.out.println(login);
                                intent = new Intent(getBaseContext(),AuthActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getBaseContext(),"Вы вышли",Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void InfoAlert(boolean status){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(status?"Оплата произведена успешна":"Оплата оплата не прошла")
                .setIcon(status?R.drawable.ic_checked:R.drawable.ic_cancel)
                .setCancelable(true)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToMain();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // calculate sum from selected items then send to server
    void calculateResult(String oil,String paymentType){

        if (!sum.getText().toString().equals("")) {
            int oilPrice = 0;
            int price = 0;
            int payType = Integer.parseInt(sum.getText().toString());
            int total = 0;

            for (Map.Entry<String, Integer> entry : priceList.entrySet()) {
                if (entry.getKey().equals(oilType)) {
                    oilPrice = entry.getValue();
                }
            }
            if (paymentType.equals("Тенге")) {
                total = payType / oilPrice;
                price = payType;

            }
            else if (paymentType.equals("Литры")) {
                total = payType;
                price = oilPrice * payType;

            }

            System.out.println(payType + " --- sum");
            System.out.println(total + " --- total ");
            int localShiftId = preferences.getInt(SHIFT_ID, 0);

            if(localShiftId != 0) {
                System.out.println(total + " --- total ");
                System.out.println(personID + " --- personId ");
                System.out.println(oilPrice + " --- oil Price ");
                System.out.println(oil + " --- oil ");

                CashierPayment cashierPayment = new CashierPayment(personID, localShiftId,price, ACCOUNT, oil, total);
                doPayment(cashierPayment);
                System.out.println(cashierPayment.toString() + " ----- after payment Info ----------");
            }else{
                Toast.makeText(getBaseContext(),"У вас нету Shift ID",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getBaseContext(),"ВВЕДИТЕ СУММУ",Toast.LENGTH_SHORT).show();
        }
    }

    void doPayment(CashierPayment cashierPayment){
        prDialog = ProgressDialog.show(this, "Подождите", "Загружаем ...");
        Call<String> call = getInstance().getApiService().doPayment(cashierPayment);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        System.out.println(response.body());

                        if (response.body().contains("true")) {
                            // remove user from queue
                            deleteUserRequest();
                            // show alert
                            InfoAlert(true);
                            Toast.makeText(getBaseContext(), "Оплата прошла успешно!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            InfoAlert(false);
                            Toast.makeText(getBaseContext(), "Оплата Неосуществлёна", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_LONG).show();
                    Log.d("DONE !!!!! POST ", "Refund is bad" + response.errorBody());
                }
                prDialog.dismiss();
                if (sum.getText() != null)
                    sum.setText(null);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getBaseContext(),"Серверная ошибка - "+t.getMessage(),Toast.LENGTH_LONG).show();
                Log.d("DONE !!!!! POST ", "SERVER error");
                prDialog.dismiss();
                if (sum.getText() != null)
                    sum.setText(null);
            }
        });

    }
    public void deleteUserRequest(){

        if (paymentID != 0){
            Log.d("onResponse:", " !!!!! ID -------" +paymentID);
            Payment payment = new Payment(paymentID);
            Call<String> call = getInstance().getApiService().doDeleteUserPayment(payment);
            call.enqueue(new Callback <String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().contains("true")){
                                Toast.makeText(getBaseContext(), "Успешно.", Toast.LENGTH_LONG).show();
                            }
                            else if (response.body().contains("false")){
                                Toast.makeText(getBaseContext(),"Не прошла",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getBaseContext(),"Серверная ошибка",Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_LONG).show();
                        Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getBaseContext(),"SERVER ошибка",Toast.LENGTH_SHORT).show();
                    Log.d("OnFailure ---- ERROR ", t.getMessage());
                }
            });
        }
    }
    void openShift(StartShift shiftIdRequest){
        prDialog = ProgressDialog.show(this, "Подождите", "Загружаем ...");

        Call<StartShift> call = getInstance().getApiService().openShift(shiftIdRequest);
        call.enqueue(new Callback<StartShift>() {
            @Override
            public void onResponse(Call<StartShift> call, Response<StartShift> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        System.out.println(response.body().getShiftId() + " ---------------");

                        int newShiftId = response.body().getShiftId();
                        if (shiftId == 0){
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                            editor.putInt(SHIFT_ID,newShiftId);
                            editor.commit();
                        }
                        Toast.makeText(getBaseContext(),"Смена начата",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_LONG).show();
                    Log.d("DONE !!!!! POST ", "Refund is bad" + response.message());
                }
                prDialog.dismiss();
            }
            @Override
            public void onFailure(Call<StartShift> call, Throwable t) {
                Toast.makeText(getBaseContext(),"Серверная ошибка"+t.getMessage(),Toast.LENGTH_LONG).show();
                Log.d("DONE !!!!! POST ", "SERVER error"+t.getMessage());
                prDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (prDialog != null)
            prDialog.cancel();
    }

    void autoConnection(){

        try{
            FindBluetoothDevice();
            openBluetoothPrinter();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    void FindBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
            }
            if(bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
            }
            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size() > 0){
                for(BluetoothDevice pairedDev:pairedDevice){
                    Log.d("PRINTER","------------ " + pairedDev.getName());
                    Log.d("PRINTER","------------ " + pairedDev.getUuids());
                    // My Bluetoth printer name is BTP_F09F1A
                    if(pairedDev.getName().equals("InnerPrinter")){
                        bluetoothDevice = pairedDev;
                        break;
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    void openBluetoothPrinter() throws IOException{
        try{
//            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenData();
        }catch (Exception ex){
        }
    }

    void printData(ShiftIdResponse shiftIdResponse) throws  IOException{

        for (int i = 0; i < shiftIdResponse.getTransactions().size(); i++) {
            try {
           String msg = " --------- BSG INVEST --------- \n\n" +
                        "ID         :  " + shiftIdResponse.getTransactions().get(i).getId() + " \n" +
                        "Date       :  " + shiftIdResponse.getTransactions().get(i).getDate() + " \n" +
                        "Price      :  " + shiftIdResponse.getTransactions().get(i).getPrice() + " tg \n" +
                        "Balance    :  " + shiftIdResponse.getTransactions().get(i).getBalance() + " \n" +
                        "Gas        :  " + shiftIdResponse.getTransactions().get(i).getGas() + " \n" +
                        "Login      :  " + shiftIdResponse.getTransactions().get(i).getLogin() + " \n" +
                        "PriceLiter :  " + shiftIdResponse.getTransactions().get(i).getLiters() + " tg or lt\n" +
                        "------------------------------\n\n\n";
                outputStream.write(msg.getBytes());
//                status_printer.setText("Распечатка...");
//                status_printer.setText("Принтер готов");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void beginListenData(){
        try{
            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable > 0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i = 0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b == delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
//                                                status_printer.setText(data);
                                                System.out.println("Status of printer");
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }
                }
            });
            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

