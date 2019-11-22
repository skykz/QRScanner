package com.example.qrscanner.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.R;
import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.UserPaymentResponse;
import com.example.qrscanner.model.UserPaymentsDepartment;
import com.example.qrscanner.model.Users;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.qrscanner.network.RetrofitMain.getInstance;
import static com.example.qrscanner.utils.Constants.CASHIER_ID;
import static com.example.qrscanner.utils.Constants.LOGIN;
import static com.example.qrscanner.utils.Constants.MY_PREF;
import static com.example.qrscanner.utils.Constants.PASSWORD;
import static com.example.qrscanner.utils.Constants.PAYMENT_ID;
import static com.example.qrscanner.utils.Constants.PERSON_ID;
import static com.example.qrscanner.utils.Constants.SHIFT_ID;
import static com.example.qrscanner.utils.Constants.USER_DATA;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRoute,btnReturn,btnDeleteUser,btnPrint;
    private Intent intent;
    private ProgressDialog prDialog;

    private ShiftIdResponse shiftIdResponse;
    boolean permissionDone = false;
    private TextView status_printer;
    static int paymentId = 0,personId = 0;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private Users users;
    private Gson gson;
    int shiftId;
    private SharedPreferences prefs;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        btnRoute = findViewById(R.id.buttonRoute);
        btnReturn = findViewById(R.id.buttonReturn);
        btnDeleteUser = findViewById(R.id.buttonDeleteUser);
        btnPrint = findViewById(R.id.buttonPrint);

        //init button click listener
        btnReturn.setOnClickListener(this);
        btnRoute.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        // text field to set state of printer
        status_printer = findViewById(R.id.status_printer);
        autoConnection();

        gson = new Gson();
        prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);

        String login = prefs.getString(LOGIN, "----- NO Login \n");//"No name defined" is the default value.
        shiftId = prefs.getInt(SHIFT_ID, 0);//"No name defined" is the default value.
        String json = prefs.getString(USER_DATA, "----- no Object\n");

        users = gson.fromJson(json, Users.class);
        System.out.println(users + "- ------ test Object");

    }

    void autoConnection(){

        try{
            FindBluetoothDevice();
            openBluetoothPrinter();
            permissionDone = true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    void FindBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                status_printer.setText("No Bluetooth Adapter found");
                status_printer.setTextColor(Color.RED);
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
                        status_printer.setText("Bluetooth подключен: "+pairedDev.getName());
                        status_printer.setTextColor(Color.GREEN);
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
                        "GAS        :  " + shiftIdResponse.getTransactions().get(i).getGas() + " \n" +
                        "Login      :  " + shiftIdResponse.getTransactions().get(i).getLogin() + " \n" +
                        "PriceLiter :  " + shiftIdResponse.getTransactions().get(i).getLiters() + " \n" +
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
                                                status_printer.setText(data);
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

    // onClick button function by Id of button
    @Override
    public void onClick(View v) {
        Log.d("----------------","clicked button ");
        switch (v.getId()) {
            case R.id.buttonReturn: /// button to return money
                intent = new Intent(this, MoneyBackActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonRoute:
                int departmentId = 73;// this is a fake id to user payment post request like departmentId  73, it will be changed in future
                UserPaymentsDepartment userPaymentsDepartment = new UserPaymentsDepartment(departmentId);
                sendRequestUserPayment(userPaymentsDepartment);
                break;
            case R.id.buttonPrint: // button to print our result form server to Printer
                sendRequestToPrint(shiftId);
                break;
            case R.id.buttonDeleteUser:
                departmentId = 73;// delete user from queue if exists
                userPaymentsDepartment = new UserPaymentsDepartment(departmentId);
                sendRequestUserPaymentSecond(userPaymentsDepartment);
                break;
            case R.id.toolbar_navigator:
                onBackPressed();
                break;
        }
    }
    // main request to get data then go forward to main page
    public void sendRequestUserPayment(UserPaymentsDepartment userPaymentsDepartment){
        prDialog = ProgressDialog.show(NavigationActivity.this, "Подождите", "Загружаем ...");

        Call<List<UserPaymentResponse>>  call = getInstance().getApiService().getUserPaymentsDepId(userPaymentsDepartment);
        call.enqueue(new Callback <List<UserPaymentResponse>>() {
            @Override
            public void onResponse(Call<List<UserPaymentResponse>> call, Response<List<UserPaymentResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(getBaseContext(),"Загрузка...",Toast.LENGTH_SHORT).show();
                        if (response.body().size() != 0){ // checking size of got array
                            int paymentId = response.body().get(0).getPaymentId();
                            int personId = response.body().get(0).getPersonId();
                            System.out.println("-------------  payment Id " + paymentId + " , person Id " + personId);

                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                            editor.putInt(PAYMENT_ID, paymentId);
                            editor.putInt(PERSON_ID,personId);
                            editor.commit();

                            intent = new Intent(NavigationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getBaseContext(),"Нету данных, чтобы выполнить оплату",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // сервер вернул ошибку
                    Log.d(" error !!!!! ошибка ", "Response is invalid");
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_SHORT).show();
                }
                prDialog.dismiss();
            }
            @Override
            public void onFailure(Call<List<UserPaymentResponse>> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER Ошибка",Toast.LENGTH_SHORT).show();
                Log.d("DONE !!!!! ошибка ", t.getMessage());
                prDialog.dismiss();
            }
        });
    }

    // request to delete last user from queue and return to back

    public void sendRequestUserPaymentSecond(UserPaymentsDepartment userPaymentsDepartment){
        prDialog = ProgressDialog.show(NavigationActivity.this, "Подождите", "Загружаем ...");
        prDialog.show();

        Call<List<UserPaymentResponse>>  call = getInstance().getApiService().getUserPaymentsDepId(userPaymentsDepartment);
        call.enqueue(new Callback <List<UserPaymentResponse>>() {
            @Override
            public void onResponse(Call<List<UserPaymentResponse>> call, Response<List<UserPaymentResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() != 0){
                             paymentId = response.body().get(0).getPaymentId();
                             personId = response.body().get(0).getPersonId();
                             System.out.println(paymentId+" ==============");

                             deleteUserRequest(paymentId);

                        }else{
                            Toast.makeText(getBaseContext(),"Пользователей не существуют!",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d(" error !!!!! ошибка ", "Response is invalid");
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_SHORT).show();
                }
                prDialog.dismiss();
            }
            @Override
            public void onFailure(Call<List<UserPaymentResponse>> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER Ошибка",Toast.LENGTH_SHORT).show();
                Log.d("DONE !!!!! ошибка ", t.getMessage());
            }
        });
    }

    public void deleteUserRequest(int paymentId){

        if (paymentId != 0){
            Log.d("onResponse:", " !!!!! ID -------" +paymentId);
            Payment payment = new Payment(paymentId);
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

    private void sendRequestToPrint(int shiftID){

        if (shiftID != 0) {
            prDialog = ProgressDialog.show(NavigationActivity.this, "Подождите", "Печать чека ...");

            Call<ShiftIdResponse> call = getInstance().getApiService().getShiftData(shiftID);
            call.enqueue(new Callback<ShiftIdResponse>() {
                @Override
                public void onResponse(Call<ShiftIdResponse> call, Response<ShiftIdResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {

                            shiftIdResponse = response.body();
                            if (shiftIdResponse.getTransactions().size() == 0)
                                Toast.makeText(getBaseContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getBaseContext(), "Распечатан", Toast.LENGTH_SHORT).show();

                            try {
                                printData(shiftIdResponse);

                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(), "Ошибка во время распечатки", Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }
                        }
                        prDialog.dismiss();
                    } else {
                        Toast.makeText(getBaseContext(), "Ответ у нас пустой", Toast.LENGTH_SHORT).show();
                        Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
                        prDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ShiftIdResponse> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "SERVER error", Toast.LENGTH_SHORT).show();
                    Log.d("OnFailure ---- ERROR ", t.getMessage());
                }
            });
        }else{
            Toast.makeText(getBaseContext(), "Нет открытой смены", Toast.LENGTH_SHORT).show();
        }
    }

}
