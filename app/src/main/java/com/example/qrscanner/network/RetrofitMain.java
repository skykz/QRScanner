package com.example.qrscanner.network;

import android.app.Application;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.qrscanner.utils.Constants.BASE_URL;

public class RetrofitMain extends Application{

        public static RetrofitMain instance;

        private SharedPreferences preferences;

        public static Retrofit retrofit;

        public static ApiService apiService;

        @Override
        public void onCreate() {

            super.onCreate();
            instance = this;
            preferences = getSharedPreferences("app", MODE_PRIVATE);

            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

        public static RetrofitMain getInstance() {
            return instance;
        }

        public SharedPreferences getPreferences() {
            return preferences;
        }

        public ApiService getApiService(){ return apiService; }
    }
