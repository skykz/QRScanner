package com.example.qrscanner.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.R;
import com.example.qrscanner.scanner.ScannerActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    //    ProgressDialog dialog;
    private WebView myWebView;
    ProgressDialog prDialog;
    private Intent intent;


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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        prDialog = ProgressDialog.show(MainActivity.this, null, "Загрузка, подождите ...");


        WebView browser = new WebView(this);
        browser.getSettings().setJavaScriptEnabled(true);

        browser.loadUrl(getIntent().getExtras().getString("url"));

        setContentView(browser);
        WebSettings ws = browser.getSettings();
        ws.setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new Object() {
            @JavascriptInterface // For API 17+
            public void performClick(String strl) {
                Toast.makeText(MainActivity.this, strl, Toast.LENGTH_SHORT).show();

                if (strl != null) {
                    startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                    finish();
                }

            }
        }, "ok");

        browser.setWebViewClient(new WebViewClient() {

//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//                super.onPageStarted(view, url, favicon);
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prDialog.dismiss();
                super.onPageFinished(view, url);
            }
        });
    }
}
//
//
//
////        progressBar = new ProgressBar(MainActivity.this,null,android.R.attr.progressBarStyleLarge);
////        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(100,100);
////        params.addRule(ConstraintLayout.CENTER_IN_PARENT);
////        layout.addView(progressBar,params);
////        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
//
//
////         myWebView = (WebView) findViewById(R.id.webview);
////         myWebView.loadUrl("https://subshop.kz/");
////
//////        progressBar.setVisibility(View.GONE);
////
////         myWebView.getSettings().setJavaScriptEnabled(true);//
////
////        myWebView.addJavascriptInterface(valid, "valid");
//////        myWebView.addJavascriptInterface(refuse, "refuse");
////
////
////
////         myWebView.setWebViewClient(new WebViewClient());
////         myWebView.setWebChromeClient(new WebChromeClient(){
////
////             @Override
////             public void onProgressChanged(WebView view, int newProgress) {
////                 super.onProgressChanged(view, newProgress);
////             }
////
////             @Override
////             public void onReceivedTitle(WebView view, String title) {
////                 super.onReceivedTitle(view, title);
////             }
////
////             @Override
////             public void onReceivedIcon(WebView view, Bitmap icon) {
////                 super.onReceivedIcon(view, icon);
////             }
////         });
//
//
////         dialog.dismiss();
//
////        mTextMessage = (TextView) findViewById(R.id.message);
////        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
////        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//    }
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
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        prDialog.dismiss();
//        prDialog.cancel();
//    }

