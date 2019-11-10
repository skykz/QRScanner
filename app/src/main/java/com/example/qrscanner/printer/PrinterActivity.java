package com.example.qrscanner.printer;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qrscanner.R;
import java.util.HashMap;
import java.util.Iterator;

public class PrinterActivity extends AppCompatActivity {

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbInterface mInterface;
    private UsbEndpoint mEndPoint;
    private PendingIntent mPermissionIntent;
    private EditText ed_txt;
    private static final String ACTION_USB_PERMISSION = "com.example.qrscanner.printer.USB_PERMISSION";
    private static Boolean forceCLaim = true;
    private HashMap<String, UsbDevice> mDeviceList;
    private Iterator<UsbDevice> mDeviceIterator;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);


        ed_txt = (EditText) findViewById(R.id.ed_txt);
        Button print = (Button) findViewById(R.id.print);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();

        if (mDeviceList.size() > 0) {
            mDeviceIterator = mDeviceList.values().iterator();

            Toast.makeText(this, "Device List Size: " + String.valueOf(mDeviceList.size()), Toast.LENGTH_SHORT).show();

            TextView textView = (TextView) findViewById(R.id.usbDevice);
            String usbDevice = "";

            while (mDeviceIterator.hasNext()) {
                UsbDevice usbDevice1 = mDeviceIterator.next();
                usbDevice += "\n" +
                        "DeviceID: " + usbDevice1.getDeviceId() + "\n" +
                        "DeviceName: " + usbDevice1.getDeviceName() + "\n" +
                        "Protocol: " + usbDevice1.getDeviceProtocol() + "\n" +
                        "Product Name: " + usbDevice1.getProductName() + "\n" +
                        "Manufacturer Name: " + usbDevice1.getManufacturerName() + "\n" +
                        "DeviceClass: " + usbDevice1.getDeviceClass() + " - " + translateDeviceClass(usbDevice1.getDeviceClass()) + "\n" +
                        "DeviceSubClass: " + usbDevice1.getDeviceSubclass() + "\n" +
                        "VendorID: " + usbDevice1.getVendorId() + "\n" +
                        "ProductID: " + usbDevice1.getProductId() + "\n"+
                        "Device interface :" + usbDevice1.getInterfaceCount();

//                int interfaceCount = usbDevice1.getInterfaceCount();
//                Toast.makeText(this, "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

                mDevice = usbDevice1;

                Toast.makeText(this, "Устройство подключено", Toast.LENGTH_SHORT).show();
                textView.setText(usbDevice);
            }

            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);

            mUsbManager.requestPermission(mDevice, mPermissionIntent);
        } else {
            Toast.makeText(this, "Пожалуйста, подключите принтер через USB", Toast.LENGTH_SHORT).show();
        }

        print.setOnClickListener(view -> {
            printMessage(getBaseContext(),"Unfortunately the printer prints a lot of strange characters (approx. 50 cm of paper). I don't know how to print the image. ");
//                print(mConnection, mInterface);
        });
    }

    @SuppressLint("NewApi")
    public void closeConnection(Context context){
        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        Toast.makeText(context, "Device closed", Toast.LENGTH_SHORT).show();
                        mConnection.close();
                    }
                }
            }
        };
    }

    @SuppressLint("NewApi")
    public void printMessage(Context context,String msg) {
        // TODO Auto-generated method stub
        final String printdata = msg;
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
                        {
//                            Log.e("Connection:"," connected");
                            Toast.makeText(context, "Device connected", Toast.LENGTH_SHORT).show();
                        }
                        boolean forceClaim = true;
                        mConnection.claimInterface(intf, forceClaim );
                        Integer res = mConnection.bulkTransfer(mEndpointBulkOut, printdata.getBytes(), printdata.getBytes().length, 10000);
                        new Thread(() -> {
                            // TODO Auto-generated method stub
//                            Log.i("Thread:", "in run thread");
//                            byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
                            byte[] bytes = printdata.getBytes();
                            int b = mConnection.bulkTransfer(mEndpointBulkOut, bytes, bytes.length, 100000);
//                            int c = mConnection.bulkTransfer(mEndpointBulkOut, cut_paper, cut_paper.length, 100);
                            Log.i("Return Status", "b ------------------>" + b);
//                            Log.i("Return Status", "b-->" + c);
                        }).start();

                        mConnection.releaseInterface(intf);
                        break;
                    }
                }
            }
        }else{
            mUsbManager.requestPermission(mDevice, mPermissionIntent);
            Toast.makeText(context, "У принтера нету доступа. Разрешите", Toast.LENGTH_SHORT).show();
        }
    }
//    private void print(final UsbDeviceConnection connection, final UsbInterface usbInterface) {
//
//        //text from layout editText
//        final String test = ed_txt.getText().toString() + "\n\n";
//
//        testBytes = test.getBytes();
//
//        if (usbInterface == null) {
//            Toast.makeText(this, "INTERFACE IS NULL", Toast.LENGTH_SHORT).show();
//        } else if (connection == null) {
//            Toast.makeText(this, "CONNECTION IS NULL", Toast.LENGTH_SHORT).show();
//        } else if (forceCLaim == null) {
//            Toast.makeText(this, "FORCE CLAIM IS NULL", Toast.LENGTH_SHORT).show();
//        } else {
//
//            Toast.makeText(this, "Please attach printer via USB" + usbInterface, Toast.LENGTH_SHORT).show();
//
//            connection.claimInterface(usbInterface, forceCLaim);
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    byte[] cut_paper = {0x1D, 0x56, 0x41, 0x10};
//                    connection.bulkTransfer(mEndPoint, testBytes, testBytes.length, 0);
//                    connection.bulkTransfer(mEndPoint, cut_paper, cut_paper.length, 0);
//                }
//            });
//            thread.run();
//        }
//        Intent intent = new Intent(this,PaymentStateActivity.class);
//            intent.putExtra("state",testBytes.length);
//                startActivity(intent);
//
//    }


    final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    Log.d("PrinterManager", "Device Attached: " + device.getDeviceName() + " " + device.getProductId() + " " + device.getVendorId());
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndPoint = mInterface.getEndpoint(0);
                            mConnection = mUsbManager.openDevice(device);
                            Toast.makeText(context, "PERMISSION Goood _", Toast.LENGTH_SHORT).show();

                            //setup();
                        }
                    } else {
                        //Log.d("SUB", "permission denied for device " + device);
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

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

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Введите пароль, чтобы выйти ")
                .setCancelable(false)
                .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PrinterActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Назад", null)
                .show();
    }

}