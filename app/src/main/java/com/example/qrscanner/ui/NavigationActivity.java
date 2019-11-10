package com.example.qrscanner.ui;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.renderscript.Sampler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.R;
import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.ShiftIdRequest;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.UserPaymentResponse;
import com.example.qrscanner.model.UserPaymentsDepartment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.example.qrscanner.network.RetrofitMain.getInstance;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRoute,btnReturn,btnDeleteUser,btnPrint;
    private String mainPassword = "123456";// main password to exit
    final Context context = this;
    private Intent intent;
    private ProgressDialog prDialog;

    //usb variables ton use it
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbInterface mInterface;
    private UsbEndpoint mEndPoint;
    private PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION = "com.example.qrscanner.printer.USB_PERMISSION";
    private HashMap<String, UsbDevice> mDeviceList;
    private Iterator<UsbDevice> mDeviceIterator;
    private ShiftIdResponse shiftIdResponse;
    boolean permissionDone = false;
    private TextView status_printer;
    static int paymentId = 0,personId = 0;


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

        // usb manager to detect
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();

        if (mDeviceList.size() > 0) {
            mDeviceIterator = mDeviceList.values().iterator();
            if (mDeviceIterator.hasNext()) {
                mDevice = mDeviceIterator.next();
                Toast.makeText(this, "Устройство подключено " + mDevice.getProductName(), Toast.LENGTH_SHORT).show();
            }

            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mUsbReceiver, filter);

            mUsbManager.requestPermission(mDevice, mPermissionIntent);
        } else {
            Toast.makeText(this, "Пожалуйста, подключите принтер через USB", Toast.LENGTH_SHORT).show();
        }
    }


    // this part of code can be changed to Bluetooth connection to printer
    final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            permissionDone = true;
                            mInterface = device.getInterface(0);
                            mEndPoint = mInterface.getEndpoint(0);
                            mConnection = mUsbManager.openDevice(device);
                            status_printer.setTextColor(getResources().getColor(R.color.colorConnected));
                            status_printer.setText("Подключен ");
                            Toast.makeText(context, "Разрешение получено", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        status_printer.setTextColor(getResources().getColor(R.color.colorDisconnected));
                        status_printer.setText("Не подключен");
//                        Log.d("SUB", "permission denied for device _______________ " + device);
                        Toast.makeText(context, "Разрешение Не получено", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                // Device removed
                synchronized (this) {
                    permissionDone = false;
                    status_printer.setText("Удалено");
                    status_printer.setTextColor(getResources().getColor(R.color.colorDisconnected));
                    Toast.makeText(context, "Устройство удалено", Toast.LENGTH_SHORT).show();
                }
            }
            if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // Device attached
                synchronized (this) {
                    permissionDone = true;
                    // Qualify the new device to suit your needs and request permission
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    status_printer.setText("Подключено");
                    status_printer.setTextColor(getResources().getColor(R.color.colorAttached));
                    Toast.makeText(context, "Устройство подключено", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };


    // onClick button function by Id of button
    @Override
    public void onClick(View v) {
        Log.d("----------------","clicked button ");

        switch (v.getId()) {
            case R.id.buttonReturn: /// button to return money
                intent = new Intent(this, MoneyBackActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonRoute: // button to go to main page with webView
                int departmentId = 73;// this is a fake id to user payment post request like departmentId  73, it will be changed in future
                UserPaymentsDepartment userPaymentsDepartment = new UserPaymentsDepartment(departmentId);
                sendRequestUserPayment(userPaymentsDepartment);
                break;
            case R.id.buttonPrint: // button to print our result form server to Printer
                prDialog.show();
                if (permissionDone) { //checking connection state of printer permission
                    Toast.makeText(context, "Распечатка...", Toast.LENGTH_SHORT).show();

                    sendRequestPayment();
                    prDialog.dismiss();
                }else{
                    prDialog.dismiss();
                    Toast.makeText(this, "нужен permission", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonDeleteUser: // delete user from queue if exists
                    departmentId = 73;
                 userPaymentsDepartment = new UserPaymentsDepartment(departmentId);
                sendRequestUserPaymentSecond(userPaymentsDepartment);

                deleteUserRequest();
                break;
        }
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

    // main request to get data then go forward to main page
    public void sendRequestUserPayment(UserPaymentsDepartment userPaymentsDepartment){

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
                            intent = new Intent(NavigationActivity.this, MainActivity.class);
                            intent.putExtra("paymentId", String.valueOf( paymentId));
                            intent.putExtra("personId", String.valueOf( personId));
                            startActivity(intent);
                        }else{
                            // if array is equal to 0, just go to main page without parameters
                            intent = new Intent(NavigationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    // сервер вернул ошибку
                    Log.d(" error !!!!! ошибка ", "Response is invalid");
                    Toast.makeText(getBaseContext(),"Неизвестная ошибка",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<UserPaymentResponse>> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER Ошибка",Toast.LENGTH_SHORT).show();
                Log.d("DONE !!!!! ошибка ", t.getMessage());
            }
        });
    }

    // request to delete last user from queue and return to back
    public void deleteUserRequest(){
        if (paymentId != 0){

            Log.d("onResponse:", " !!!!! ID -------" +paymentId);

            Payment payment = new Payment(paymentId);

            Call<String> call = getInstance().getApiService().doPayment(payment);
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

    public void sendRequestUserPaymentSecond(UserPaymentsDepartment userPaymentsDepartment){

        prDialog = ProgressDialog.show(NavigationActivity.this, "Подождите", "Загружаем ...");
        prDialog.show();

        Call<List<UserPaymentResponse>>  call = getInstance().getApiService().getUserPaymentsDepId(userPaymentsDepartment);
        call.enqueue(new Callback <List<UserPaymentResponse>>() {
            @Override
            public void onResponse(Call<List<UserPaymentResponse>> call, Response<List<UserPaymentResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(getBaseContext(),"Загрузка...",Toast.LENGTH_SHORT).show();

                        if (response.body().size() != 0){
                             paymentId = response.body().get(0).getPaymentId();
                             personId = response.body().get(0).getPersonId();
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


    //main function to pass message to printer
    @SuppressLint("NewApi")
    public void printMessage(Context context,int id,int price,String date,String balance,String gas,String login,int liters) {

        final UsbEndpoint mEndpointBulkOut;
        if (mUsbManager.hasPermission(mDevice)){
            UsbInterface intf = mDevice.getInterface(0);

            for (int i = 0; i < intf.getEndpointCount(); i++) {
                UsbEndpoint ep = intf.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {

                        mEndpointBulkOut = ep;
                        mConnection = mUsbManager.openDevice(mDevice);

                        if(mConnection!=null)
                            Toast.makeText(context, "Принтер подключен!", Toast.LENGTH_SHORT).show();

                        boolean forceClaim = true;
                        mConnection.claimInterface(intf, forceClaim);

                        String txt = " --------- BSG INVEST --------- \n\n" +
                                "ID      :  "+id+" \n" +
                                "Date    :  "+date+" \n" +
                                "Price   :  "+price+" tg \n" +
                                "Balance :  "+balance+" \n" +
                                "Toplivo :  "+gas+" \n"+
                                "Login   :  "+login+" \n"+
                                "Liters  :  "+liters+" lt\n"+
                                "------------------------------\n\n\n\n";
                        new Thread(() -> {
                            byte[] bytes = txt.getBytes();
//                        int b = mConnection.bulkTransfer(mEndpointBulkOut, setJsonToPrinter(id, price, balance, gas, date, login,liters), setJsonToPrinter(id, price, balance, gas, date, login,liters).length, 100);
                            mConnection.bulkTransfer(mEndpointBulkOut, bytes, bytes.length, 1000);
                        }).start();
                        mConnection.releaseInterface(intf);
                        break;
                    }
                }
            }
        }else{
            mUsbManager.requestPermission(mDevice, mPermissionIntent);
            Toast.makeText(context, "Устройство не имеет разрешения!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequestPayment(){
        ShiftIdRequest shiftIdRequest = new ShiftIdRequest(550);

        Call<ShiftIdResponse> call = getInstance().getApiService().getShiftData(shiftIdRequest);
        call.enqueue(new Callback <ShiftIdResponse>() {
            @Override
            public void onResponse(Call<ShiftIdResponse> call, Response<ShiftIdResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        Log.d("onResponse -- ",response.message());
                        Toast.makeText(getBaseContext(),"Успешно",Toast.LENGTH_SHORT).show();

                        int id = response.body().getTransactions().get(0).getId();
                        String date = response.body().getTransactions().get(0).getDate();
                        int price = response.body().getTransactions().get(0).getPrice();
                        String balance = response.body().getTransactions().get(0).getBalance();
                        String gas = response.body().getTransactions().get(0).getGas();
                        String login = response.body().getTransactions().get(0).getLogin();
                        int liters = response.body().getTransactions().get(0).getLiters();

                        printMessage(context,id, price,date, balance, gas, login,liters);

                    }
                } else {
                    Toast.makeText(getBaseContext(),"Ответ у нас пустой",Toast.LENGTH_SHORT).show();
                    Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
                }
            }
            @Override
            public void onFailure(Call<ShiftIdResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SERVER error",Toast.LENGTH_SHORT).show();
                Log.d("OnFailure ---- ERROR ", t.getMessage());
            }
        });

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
