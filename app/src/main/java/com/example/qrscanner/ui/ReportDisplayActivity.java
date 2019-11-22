package com.example.qrscanner.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.qrscanner.R;
import com.example.qrscanner.adapters.listViewAdapter;
import com.example.qrscanner.model.ShiftIdResponse;
import com.example.qrscanner.model.Transactions;


import java.util.List;

import static com.example.qrscanner.utils.Constants.CASHIER_ID;
import static com.example.qrscanner.utils.Constants.LOGIN;
import static com.example.qrscanner.utils.Constants.MY_PREF;
import static com.example.qrscanner.utils.Constants.PASSWORD;
import static com.example.qrscanner.utils.Constants.PAYMENT_ID;
import static com.example.qrscanner.utils.Constants.PERSON_ID;
import static com.example.qrscanner.utils.Constants.USER_DATA;


public class ReportDisplayActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView typeReport,titleReport,infoReport,txtDate,txtPrice,txtSum,txtOil,txtLogin,txtLiter;
    private Toolbar toolbar;
    private Button btnLogout;
    ShiftIdResponse shiftIdResponse;
    private SharedPreferences prefs;
    List<Transactions> transactions;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_display);
        toolbar = (Toolbar)findViewById(R.id.toolbar_report);
        typeReport = (TextView)findViewById(R.id.type_report);
        titleReport = (TextView)findViewById(R.id.title_report);
        infoReport = (TextView)findViewById(R.id.info_report);
        btnLogout = (Button) findViewById(R.id.btnLogout);


        toolbar.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        Intent intent = getIntent();

        prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);

        if (getIntent() != null && getIntent().getExtras() != null){
            String X = intent.getStringExtra("title");
            System.out.println(X);
            typeReport.setText(X);

            if(X.equals("X - Отчет"))
            btnLogout.setVisibility(View.GONE);

            shiftIdResponse = (ShiftIdResponse) getIntent().getSerializableExtra("reportData");
        }
        titleReport.setText(shiftIdResponse.getDepartmentName() + "( Прибыль: " + shiftIdResponse.getProfit() + "KZT, Покупок: 0 )");
        infoReport.setText("Дата Начала: "+shiftIdResponse.getStartDate());



        transactions  = shiftIdResponse.getTransactions();
        ListView listView = (ListView) findViewById(R.id.listview);
        listViewAdapter adapter = new listViewAdapter(this, transactions);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnLogout:
                prefs.edit().remove(LOGIN).commit();
                prefs.edit().remove(PASSWORD).commit();
                prefs.edit().remove(PERSON_ID).commit();
                prefs.edit().remove(PAYMENT_ID).commit();
                prefs.edit().remove(USER_DATA).commit();
                prefs.edit().remove(CASHIER_ID).commit();
                String login = prefs.getString(LOGIN, "----- NO Login \n");//"No name defined" is the default value.
                System.out.println(login);
                Intent intent = new Intent(this,AuthActivity.class);
                startActivity(intent);
                finish();
                break;
        }
//        samal 2, dom 16
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent = new Intent(this, NavigationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
    }
}


