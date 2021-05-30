package com.example.deeplearningstudio;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;
    static final String COOKIES_HEADER = "Set-Cookie";
    static CookieManager msCookieManager = new CookieManager();

    @Override
    protected String doInBackground(String... params) {
        String result;
        String inputLine;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);



        return "";
    }

}
