package com.example.qrscanner.ui;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qrscanner.R;
import com.example.qrscanner.model.Payment;
import com.example.qrscanner.model.ShiftIdResponse;



import java.util.HashMap;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.example.qrscanner.network.RetrofitMain.getInstance;
import static com.example.qrscanner.utils.Constants.BALANCE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //main webView part
    private WebView myWebView;
    private ProgressDialog prDialog;
    final Context context = this;
    private Button printbtn;


    //main printer and USB parts
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
    int paymentID = 0,personID = 0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (getIntent() != null && getIntent().getExtras() != null){
            String paymentId = intent.getStringExtra("paymentId");
            String personId = intent.getStringExtra("personId");

           paymentID = Integer.valueOf(paymentId);
           personID = Integer.valueOf(personId);
        }


//        // usb manager to detect
//        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        mDeviceList = mUsbManager.getDeviceList();
//
//
//        if (mDeviceList.size() > 0) {
//            mDeviceIterator = mDeviceList.values().iterator();
//
//
//        if (mDeviceIterator.hasNext()) {
//            mDevice = mDeviceIterator.next();
//            Toast.makeText(this, "Устройство подключено " + mDevice.getProductName(), Toast.LENGTH_SHORT).show();
//
//        }
////            Toast.makeText(this, "Устройство подключено ____", Toast.LENGTH_SHORT).show();
//
//        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//            filter.addAction(ACTION_USB_DEVICE_ATTACHED);
//            filter.addAction(ACTION_USB_DEVICE_DETACHED);
//            registerReceiver(mUsbReceiver, filter);
//
//        mUsbManager.requestPermission(mDevice, mPermissionIntent);
//
//
//    } else {
//        Toast.makeText(this, "Пожалуйста, подключите принтер через USB", Toast.LENGTH_SHORT).show();
//    }
//

        ////-----------------------------------------------------------
        /// Main Activity methods
        prDialog = ProgressDialog.show(MainActivity.this, "Подождите", "Загружаем ...");


        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings ws = myWebView.getSettings();
        ws.setJavaScriptEnabled(true);

            myWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface // For API 17+
                public void performClick(String strl) {
                    Log.d("ОПЛАТИТЬ","WebView ----------- button is working!"+strl);
                    if (strl != null) {
                        prDialog.show();

                        if (paymentID != 0){
                        Payment payment = new Payment(paymentID);

                        Call<String> call = getInstance().getApiService().doPayment(payment);
                        call.enqueue(new Callback <String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {

                                        if (response.body().contains("true")){
                                            Toast.makeText(getBaseContext(), "Оплата прошла успешно.", Toast.LENGTH_LONG).show();
                                        goToMain();
                                            }
                                        else if (response.body().contains("false")){
                                            Toast.makeText(getBaseContext(),"Оплата не прошла!",Toast.LENGTH_LONG).show();
                                            goToMain();
                                        }else{
                                            Toast.makeText(getBaseContext(),"Серверная ошибка",Toast.LENGTH_LONG).show();
                                            goToMain();
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
                        });}else{
                            Toast.makeText(getBaseContext(),"Клиента не существует",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, "ok");



            myWebView.setWebViewClient(new WebViewClient(){
             @Override
             public void onPageFinished(WebView view, String url) {
                 Log.i(TAG, "Finished loading URL: " + url);
                 if (prDialog.isShowing()) {
                     prDialog.dismiss();
                 }
             }

             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 Log.i(TAG, "Processing WebView url click...");
                 view.loadUrl(url);
                 return true;
             }

             public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                 Log.e(TAG, "Error: " + description);
                 Toast.makeText(MainActivity.this, "Oh ошибка вебвью! " + description, Toast.LENGTH_SHORT).show();
             }
         });

            if (personID != 0)
                 myWebView.loadUrl("http://feligram.com:8083/cashier?id="+personID+"&accountName="+BALANCE+"");
            else
                myWebView.loadUrl("http://feligram.com:8083/cashier");

}

//    final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//            if (ACTION_USB_PERMISSION.equals(action)) {
//                synchronized (this) {
////                    Log.d("PrinterManager", "Device Attached: __________________ " + device.getDeviceName() + " " + device.getProductId() + " " + device.getVendorId());
//
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if (device != null) {
//                            //call method to set up device communication
//                            permissionDone = true;
//                            mInterface = device.getInterface(0);
//                            mEndPoint = mInterface.getEndpoint(0);
//                            mConnection = mUsbManager.openDevice(device);
//                            status_printer.setTextColor(getResources().getColor(R.color.colorConnected));
//                            status_printer.setText("Подключен ");
//                            Toast.makeText(context, "Разрешение получено", Toast.LENGTH_SHORT).show();
//
//                            //setup();
//                        }
//                    } else {
//                        status_printer.setTextColor(getResources().getColor(R.color.colorDisconnected));
//                        status_printer.setText("Не подключен");
////                        Log.d("SUB", "permission denied for device _______________ " + device);
//                        Toast.makeText(context, "Разрешение Не получено", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                // Device removed
//                synchronized (this) {
//                    permissionDone = false;
//                    status_printer.setText("Удалено");
//                    status_printer.setTextColor(getResources().getColor(R.color.colorDisconnected));
//                    Toast.makeText(context, "Устройство удалено", Toast.LENGTH_SHORT).show();
//                }
//            }
//            if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                // Device attached
//                synchronized (this) {
//                    permissionDone = true;
//                    // Qualify the new device to suit your needs and request permission
//                    mUsbManager.requestPermission(device, mPermissionIntent);
//                    status_printer.setText("Подключено");
//                    status_printer.setTextColor(getResources().getColor(R.color.colorAttached));
//                    Toast.makeText(context, "Устройство подключено", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (prDialog != null)
            prDialog.cancel();
    }

    private void goToMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(mainIntent);
            }
        }, 4000);
    }


////-------------------------------- Printer Main methods -------------------------------------
//@SuppressLint("NewApi")
//public void printMessage(Context context,int id,int price,String date,String balance,String gas,String login,int liters) {
//
//
//
//    final UsbEndpoint mEndpointBulkOut;
//
//    if (mUsbManager.hasPermission(mDevice)){
//        UsbInterface intf = mDevice.getInterface(0);
//
//        for (int i = 0; i < intf.getEndpointCount(); i++) {
//            UsbEndpoint ep = intf.getEndpoint(i);
//            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
//                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
//
//                    mEndpointBulkOut = ep;
//                    mConnection = mUsbManager.openDevice(mDevice);
//
//                    if(mConnection!=null)
//                        Toast.makeText(context, "Принтер подключен!", Toast.LENGTH_SHORT).show();
//
//                    boolean forceClaim = true;
//                    mConnection.claimInterface(intf, forceClaim);
//
//                    String txt = " --------- BSG INVEST --------- \n\n" +
//                            "ID      :  "+id+" \n" +
//                            "Date    :  "+date+" \n" +
//                            "Price   :  "+price+" tg \n" +
//                            "Balance :  "+balance+" \n" +
//                            "Toplivo :  "+gas+" \n"+
//                            "Login   :  "+login+" \n"+
//                            "Liters  :  "+liters+" lt\n"+
//                            "------------------------------\n\n\n\n";
//                    new Thread(() -> {
//                        byte[] bytes = txt.getBytes();
////                        int b = mConnection.bulkTransfer(mEndpointBulkOut, setJsonToPrinter(id, price, balance, gas, date, login,liters), setJsonToPrinter(id, price, balance, gas, date, login,liters).length, 100);
//                             mConnection.bulkTransfer(mEndpointBulkOut, bytes, bytes.length, 1000);
//                    }).start();
//                    mConnection.releaseInterface(intf);
//                    break;
//                }
//            }
//        }
//    }else{
//        mUsbManager.requestPermission(mDevice, mPermissionIntent);
//        Toast.makeText(context, "Устройство не имеет разрешения!", Toast.LENGTH_SHORT).show();
//    }
//    }

//    private void sendRequestPayment(){
//        ShiftIdRequest shiftIdRequest = new ShiftIdRequest(550);
//
//        Call<ShiftIdResponse> call = getInstance().getApiService().getShiftData(shiftIdRequest);
//        call.enqueue(new Callback <ShiftIdResponse>() {
//            @Override
//            public void onResponse(Call<ShiftIdResponse> call, Response<ShiftIdResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        Log.d("onResponse -- ",response.message());
//                        Toast.makeText(getBaseContext(),"Успешно",Toast.LENGTH_SHORT).show();
//
//                        int id = response.body().getTransactions().get(0).getId();
//                        String date = response.body().getTransactions().get(0).getDate();
//                        int price = response.body().getTransactions().get(0).getPrice();
//                        String balance = response.body().getTransactions().get(0).getBalance();
//                        String gas = response.body().getTransactions().get(0).getGas();
//                        String login = response.body().getTransactions().get(0).getLogin();
//                        int liters = response.body().getTransactions().get(0).getLiters();
//
//                        printMessage(context,id, price,date, balance, gas, login,liters);
//
//                    }
//                } else {
//                    Toast.makeText(getBaseContext(),"Refund has error",Toast.LENGTH_SHORT).show();
//                    Log.d("onResponse: !!!!! POST ", "Серверная ошибка");
//                }
//            }
//            @Override
//            public void onFailure(Call<ShiftIdResponse> call, Throwable t) {
//                Toast.makeText(getBaseContext(),"SERVER error",Toast.LENGTH_SHORT).show();
//                Log.d("OnFailure ---- ERROR ", t.getMessage());
//
//            }
//        });
//
//    }
//
//    private byte [] setJsonToPrinter(int id,int price,String balance,String gas,String date,String login,int liters){
//
//
//        StringBuilder contentSb = new StringBuilder();
//
////        String titleStr = "BSG Invest" + "\n\n";
//        contentSb.append("BSG Invest\n\n\n");
//        contentSb.append("ID :  " + id + "\n");
//        contentSb.append("Date :  "+ date + "\n");
//        contentSb.append("Price : "+ price + "\n");
//        contentSb.append("Balance :  "+ balance + "\n");
//        contentSb.append("GAS :  "+ gas + "\n");
//        contentSb.append("Login :  "+ login + "\n");
//        contentSb.append("Liters :  "+ liters + "\n\n\n\n\n");
//
////    StringBuilder content2Sb = new StringBuilder();
//
////    String jpaRef   = "XXXX-XXXX-XXXX-XXXX" + "\n";
////        String message  = "___ BSG Invest company: www.bsg.kz ___" + "\n\n\n\n\n";
////    long milsecond   = System.currentTimeMillis();
////    String date  = DateUtil.timeMilisToString(milsecond, "dd-MM-yy / HH:mm")  + "\n\n";
//
////        byte[] titleByte  = Printer.printfont(titleStr, FontDefine.FONT_48PX_HEIGHT_UNDERLINE, FontDefine.Align_CENTER,
////                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
//
//        byte[] content1Byte = Printer.printfont(contentSb.toString(), FontDefine.FONT_32PX,FontDefine.Align_LEFT,
//                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
////
////    byte[] refByte      = Printer.printfont(jpaRef, FontDefine.FONT_24PX,FontDefine.Align_CENTER,  (byte)0x1A,
////            PocketPos.LANGUAGE_ENGLISH);
////
////        byte[] messageByte  = Printer.printfont(message, FontDefine.FONT_24PX,FontDefine.Align_CENTER,  (byte)0x1A,
////                PocketPos.LANGUAGE_ENGLISH);
//
////    byte[] content2Byte = Printer.printfont(content2Sb.toString(), FontDefine.FONT_24PX,FontDefine.Align_LEFT,
////            (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
////
////    byte[] message2Byte = Printer.printfont(message2, FontDefine.FONT_24PX,FontDefine.Align_CENTER,  (byte)0x1A,
////            PocketPos.LANGUAGE_ENGLISH);
////
////    byte[] dateByte     = Printer.printfont(date, FontDefine.FONT_24PX,FontDefine.Align_LEFT, (byte)0x1A,
////            PocketPos.LANGUAGE_ENGLISH);
//
//        byte[] totalByte  = new byte[content1Byte.length ];
//        System.out.println("total byte --------- "+totalByte);
//    /*
//    -------------------------------------
//     */
//        int offset = 0;
////        System.arraycopy(titleByte, 0, totalByte, offset, titleByte.length);
////        offset += titleByte.length;
//
//        System.arraycopy(content1Byte, 0, totalByte, offset, content1Byte.length);
//        offset += content1Byte.length;
////
////    System.arraycopy(refByte, 0, totalByte, offset, refByte.length);
////    offset += refByte.length;
////
////        System.arraycopy(messageByte, 0, totalByte, offset, messageByte.length);
////        offset += messageByte.length;
////
////    System.arraycopy(content2Byte, 0, totalByte, offset, content2Byte.length);
////    offset += content2Byte.length;
////
////    System.arraycopy(message2Byte, 0, totalByte, offset, message2Byte.length);
////    offset += message2Byte.length;
////
////    System.arraycopy(dateByte, 0, totalByte, offset, dateByte.length);
//
//        /// main variable, which contains main data to print it
//        byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);
//
//        System.out.println("byte string ---------- "+sendData);
//
//        return  sendData;
//    }

    private String translateDeviceClass(int deviceClass) {

        switch (deviceClass) {

            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";

            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";

            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";

            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";

            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";

            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";

            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";

            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";

            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";

            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";

            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";

            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";

            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";

            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";

            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";

            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";

            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";

            default:
                return "Unknown USB class!";
        }
    }

    @SuppressLint("NewApi")
    public void closeConnection(Context context){
        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        Toast.makeText(context, "Принтер отключен", Toast.LENGTH_SHORT).show();
                        mConnection.close();
                    }
                }
            }
        };
    }


//    @Override
//    public void onClick(View v) {
//                prDialog.show();
//                if (permissionDone) {
//                    Toast.makeText(context, "Распечатка...", Toast.LENGTH_SHORT).show();
//                    sendRequestPayment();
//                    prDialog.dismiss();
//                }else{
//                    prDialog.dismiss();
//                    Toast.makeText(this, "нужен permission", Toast.LENGTH_LONG).show();
//                }
//    }
}

