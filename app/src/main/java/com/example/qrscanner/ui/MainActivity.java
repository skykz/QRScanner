package com.example.qrscanner.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.R;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "SplashActivitySpec";

    private TextView mTextMessage;
    private WebView myWebView;
    private ProgressDialog prDialog;
    private Intent intent;


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        prDialog = ProgressDialog.show(MainActivity.this, "Страница кассира", "Загружаем ...");


        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings ws = myWebView.getSettings();
        ws.setJavaScriptEnabled(true);
            myWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface // For API 17+
                public void performClick(String strl) {
                    Toast.makeText(MainActivity.this, strl, Toast.LENGTH_SHORT).show();

                    if (strl != null) {
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
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
                 Log.i(TAG, "Processing webview url click...");
                 view.loadUrl(url);
                 return true;               }


             public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                 Log.e(TAG, "Error: " + description);
                 Toast.makeText(MainActivity.this, "Oh ERROR! " + description, Toast.LENGTH_SHORT).show();

             }
         });
         myWebView.loadUrl("http://feligram.com:8083/cashier");


//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


}
//
////    public void loadPage (View view) {
////        WebView browser = new WebView( this ) ;
////        browser.getSettings().setJavaScriptEnabled( true ) ;
////        browser.loadUrl( "https://subshop.kz/" ) ;
////        setContentView(browser) ;
////        WebSettings ws = browser.getSettings() ;
////        ws.setJavaScriptEnabled( true ) ;
////        browser.addJavascriptInterface( new Object() {
////            @JavascriptInterface // For API 17+
////            public void performClick (String strl) {
////                Toast. makeText (MainActivity. this, strl , Toast. LENGTH_SHORT ).show() ;
////            }
////        } , "ok" ) ;
////
////        browser.setWebViewClient( new WebViewClient(){
////
////            @Override
////            public void onPageStarted(WebView view, String url, Bitmap favicon) {
////                prDialog = ProgressDialog.show(MainActivity.this, null, "Загрузка, подождите ...");
////
////                super.onPageStarted(view, url, favicon);
////            }
////
////            @Override
////            public void onPageFinished(WebView view, String url) {
////                prDialog.dismiss();
////                super.onPageFinished(view, url);
////            }
////        });
////    }
//
////    Activity parentActivity;
////    public MainActivity(Activity activity) {
////        parentActivity = activity;
////    }
////
////    public void launchNewActvity(){
////        Intent intent = new Intent(parentActivity, ScannerActivity.class);
////        parentActivity.startActivity(intent);
////    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (myWebView.canGoBack())
//        {
//            myWebView.goBack();
//        }{
//            finish();
//        }
//    }
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    startActivity(new Intent(MainActivity.this, ScannerActivity.class));
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_account:
////                    mTextMessage.setText(R.string.title_notifications);
//                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
//
//                    return true;
//            }
//            return false;
//        }
//
//    };
//        progressBar = new ProgressBar(MainActivity.this,null,android.R.attr.progressBarStyleLarge);
//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(100,100);
//        params.addRule(ConstraintLayout.CENTER_IN_PARENT);
//        layout.addView(progressBar,params);
//        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        prDialog.dismiss();
//        prDialog.cancel();
        if (prDialog != null)
            prDialog = null;
    }

}

