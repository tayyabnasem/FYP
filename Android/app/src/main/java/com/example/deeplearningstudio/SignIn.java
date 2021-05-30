package com.example.deeplearningstudio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deeplearningstudio.ui.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignIn extends AppCompatActivity {

    String signinUrl = null;

    EditText email, password;
    TextView signupText;
    Button butt, gooButt;

    InputValidatorHelper inputValidatorHelper = new InputValidatorHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = (EditText) findViewById(R.id.signInEmail);
        password = (EditText) findViewById(R.id.signInPassword);
        butt = (Button) findViewById(R.id.signInButton);
        gooButt = (Button) findViewById(R.id.googleButton);
        signupText = (TextView) findViewById(R.id.signUpText);

        JSONObject post_dict = new JSONObject();
        try {
            post_dict.put("email", email.getText().toString());
            post_dict.put("password", password.getText().toString());

            HttpRequest postReq = new HttpRequest();

            signinUrl = getResources().getString(R.string.server_url) + "signin";
            postReq.execute(signinUrl, String.valueOf(post_dict));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputValidatorHelper.isNullOrEmpty(email.getText().toString())) {
                    email.setError("Email can not be empty");
                    butt.setEnabled(false);
                } else if (!inputValidatorHelper.isValidEmail(email.getText().toString())) {
                    email.setError("Email is not valid!");
                    butt.setEnabled(false);
                } else {
                    butt.setEnabled(true);
                }

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputValidatorHelper.isNullOrEmpty(password.getText().toString())) {
                    password.setError("Email can not be empty");
                    butt.setEnabled(false);
                } else {
                    butt.setEnabled(true);
                }
            }
        });

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject post_dict = new JSONObject();
                try {
                    post_dict.put("email", email.getText().toString());
                    post_dict.put("password", password.getText().toString());

                    HttpRequest postReq = new HttpRequest();

                    signinUrl = getResources().getString(R.string.server_url) + "signin";
                    postReq.execute(signinUrl, String.valueOf(post_dict));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        gooButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

    }


    class InputValidatorHelper {
        public boolean isValidEmail(String string) {
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(string);
            return matcher.matches();
        }


        public boolean isNullOrEmpty(String string) {
            return TextUtils.isEmpty(string);
        }

        public boolean isNumeric(String string) {
            return TextUtils.isDigitsOnly(string);
        }

        //Add more validators here if necessary
    }

    public class HttpRequest extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";


        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String inputLine;
            String JsonDATA = params[1];
            try {
                SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                System.out.println(params[0]);
                // connect to the server
                URL myUrl = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                System.out.println("Received Cookie from PREF: "+pref.getString("Cookie", ""));
                connection.setRequestProperty("Cookie",
                           pref.getString("Cookies", ""));

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
                    System.out.println("Length of Cookies Header: "+cookiesHeader.size());
                    for (String cookie : cookiesHeader) {
                        System.out.println("Individual Cookie: "+HttpCookie.parse(cookie).get(0).toString());
                        editor.putString("Cookies", HttpCookie.parse(cookie).get(0).toString());
                        editor.apply();
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

            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d("System Output Req", response);
            //System.out.println("Sign In Response: " + response);
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                Log.d("System Output Req", obj.getString("text"));
                System.out.println(obj.getString("text").replace("\\", ""));

                if (obj.getString("text").equals("Logged in")) {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (obj.getString("text").equals("Already Logged in")){
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}