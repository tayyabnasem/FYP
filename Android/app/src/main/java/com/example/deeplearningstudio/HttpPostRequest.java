package com.example.deeplearningstudio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpPostRequest extends AsyncTask<String, Void, String> {
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;
    private CookieStore cookieJar;
    static final String COOKIES_HEADER = "Set-Cookie";
    static CookieManager msCookieManager = new CookieManager();


    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String inputLine;
        if (params[0].equals("GET")) {
            try {
                System.out.println(params[1]);
                // connect to the server
                URL myUrl = new URL(params[1]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    System.out.println(TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    connection.setRequestProperty("Cookie",
                            TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }
                connection.setRequestMethod("GET");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                // get the string from the input stream
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        try {
                            msCookieManager.getCookieStore().add(new URI("http://10.0.2.2:3000"), HttpCookie.parse(cookie).get(0));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
                result = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
                result = "error";
            }
        } else {
            String JsonDATA = params[2];
            try {
                System.out.println(params[1]);
                // connect to the server
                URL myUrl = new URL(params[1]);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    System.out.println(TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    connection.setRequestProperty("Cookie",
                            TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }

                connection.setRequestMethod("POST");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                // json data
                writer.close();

                // get the string from the input stream
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        try {
                            msCookieManager.getCookieStore().add(new URI("http://10.0.2.2:3000"), HttpCookie.parse(cookie).get(0));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
                result = stringBuilder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
