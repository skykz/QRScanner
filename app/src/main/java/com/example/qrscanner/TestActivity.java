package com.example.qrscanner;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Iterator;

public class TestActivity extends AppCompatActivity {

//    String actionString = "com.example.qrscanner.USB_PERMISSION";
//
//    Context context;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        getDetail();
//    }
//
//    public void getDetail() {
//        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//
//        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new
//                Intent(actionString), 0);
//
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//
//        while (deviceIterator.hasNext()) {
//            UsbDevice device = deviceIterator.next();
//
//            manager.requestPermission(device, mPermissionIntent);
//            String Model = device.getDeviceName();
//
//            int DeviceID = device.getDeviceId();
//            int Vendor = device.getVendorId();
//            int Product = device.getProductId();
//            int Class = device.getDeviceClass();
//            int Subclass = device.getDeviceSubclass();
//
//            Toast.makeText(this, "Device is " +  DeviceID, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Device is " +  Product, Toast.LENGTH_SHORT).show();
//
//        }
//    }

    UsbManager usbManager;
    PendingIntent mPermissionIntent;
    UsbDevice usbDevice;
    Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        final String ACTION_USB_PERMISSION =
                "com.example.qrscanner.USB_PERMISSION";

        IntentFilter filter = new IntentFilter("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        registerReceiver(mUsbReceiver, filter);

    }


    private static final String ACTION_USB_PERMISSION =
            "com.example.qrscanner.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {



            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {

                    usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    usbManager.requestPermission(usbDevice, mPermissionIntent);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(usbDevice != null){
                            //call method to set up device communication


                            int deviceId = usbDevice.getDeviceId();
                            int productId = usbDevice.getProductId();


                            Toast.makeText(context, "Device si " + deviceId, Toast.LENGTH_SHORT).show();
                            Log.i("device id", "****"+deviceId);
                            Log.i("product id", "****"+productId);

                        }else{
                            Log.i("device id", "No USB device");
                            Toast.makeText(context, "Device FAIL " , Toast.LENGTH_SHORT).show();

                        }

                    }
                    else {
                        Toast.makeText(context, "permission denied for device ", Toast.LENGTH_SHORT).show();

                        Log.d("shiv", "permission denied for device ");
                    }
                }
            }
        }
    };
}
