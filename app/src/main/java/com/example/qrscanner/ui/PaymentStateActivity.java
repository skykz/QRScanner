package com.example.qrscanner.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrscanner.R;

public class PaymentStateActivity extends AppCompatActivity {

    private String payment;
    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_state);

        imageView = findViewById(R.id.imageState);
        textView = findViewById(R.id.textView_status);


        if (getIntent() != null){
                textView.setText("Успешно завершено");
                imageView.setImageResource(R.drawable.ic_checked);}
            else{
                textView.setText("Ошибка");
                imageView.setImageResource(R.drawable.ic_cancel);}

    }
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
