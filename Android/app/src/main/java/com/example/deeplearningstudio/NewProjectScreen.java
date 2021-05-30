package com.example.deeplearningstudio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PageRange;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NewProjectScreen extends AppCompatActivity {

    EditText pName, pDesc;
    LinearLayout DL, ML;
    ImageView MLimg, DLimg;
    TextView dlText, mlText;
    Button next;
    private String newPID;

    String domain = "Deep Learning";

    Intent intent;

    InputValidatorHelper inputValidatorHelper = new InputValidatorHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_screen);

        pName = (EditText) findViewById(R.id.textProjName);
        pDesc = (EditText) findViewById(R.id.textProjDesc);
        DL = (LinearLayout) findViewById(R.id.optionDL);
        ML = (LinearLayout) findViewById(R.id.optionML);
        MLimg = (ImageView) findViewById(R.id.mlImage);
        DLimg = (ImageView) findViewById(R.id.dlImage);
        dlText = (TextView) findViewById(R.id.dlText);
        mlText = (TextView) findViewById(R.id.mlText);
        next = (Button) findViewById(R.id.nextButton);

        pName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputValidatorHelper.isNullOrEmpty(pName.getText().toString())) {
                    pName.setError("Name can not be empty");
                    next.setEnabled(false);
                }
                else if(inputValidatorHelper.isValidName(pName.getText().toString())){
                    pName.setError("Name is not valid");
                    next.setEnabled(false);
                }
                else {
                    next.setEnabled(true);
                }
            }
        });

        ML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ML.setBackgroundColor(getResources().getColor(R.color.active_color));

                DL.setBackgroundColor(getResources().getColor(R.color.white));

                DrawableCompat.setTint(MLimg.getDrawable(), getResources().getColor(R.color.primary_color));
                DrawableCompat.setTint(DLimg.getDrawable(), getResources().getColor(R.color.black));
                mlText.setTextColor(getResources().getColor(R.color.primary_color));
                dlText.setTextColor(getResources().getColor(R.color.black));

                domain = "Machine Learning";

            }
        });

        DL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DL.setBackgroundColor(getResources().getColor(R.color.active_color));

                ML.setBackgroundColor(getResources().getColor(R.color.white));

                DrawableCompat.setTint(DLimg.getDrawable(), getResources().getColor(R.color.primary_color));
                DrawableCompat.setTint(MLimg.getDrawable(), getResources().getColor(R.color.black));
                mlText.setTextColor(getResources().getColor(R.color.black));
                dlText.setTextColor(getResources().getColor(R.color.primary_color));

                domain = "Deep Learning";

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject post_dict = new JSONObject();
                String proj_name = pName.getText().toString();
                String proj_desc = pDesc.getText().toString();
                if (proj_name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Project Name cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        post_dict.put("name", proj_name);
                        post_dict.put("description", proj_desc);
                        post_dict.put("domain", domain);

                        CreateProject req = new CreateProject();
                        String createProjURL = getResources().getString(R.string.server_url)+"createProject";
                        req.execute(createProjURL, String.valueOf(post_dict));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private class CreateProject extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 60000;
        static final int CONNECTION_TIMEOUT = 60000;
        static final String COOKIES_HEADER = "Set-Cookie";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(NewProjectScreen.this, "Creating Project",
                    "Creating New Project. Please wait...", true);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String inputLine;
            try {
                SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                System.out.println(params[0]);
                // connect to the server
                String JsonData = params[1];
                URL myUrl = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //System.out.println("Received Cookie from PREF: "+pref.getString("Cookie", ""));
                connection.setRequestProperty("Cookie",
                        pref.getString("Cookies", ""));

                connection.setRequestMethod("POST");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(JsonData);
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
                    System.out.println("Length of Cookies Header: " + cookiesHeader.size());
                    for (String cookie : cookiesHeader) {
                        System.out.println("Individual Cookie: " + HttpCookie.parse(cookie).get(0).toString());
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
            dialog.dismiss();
            System.out.println("Create Proj Response: " + response);
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                newPID = obj.getString("projectID");
                Intent intent =new Intent(getApplicationContext(), BottomNavigationMenu.class);
                //Create the bundle
                Bundle bundle = new Bundle();

                //Add your data to bundle
                bundle.putString("projectID", newPID);

                //Add the bundle to the intent
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

