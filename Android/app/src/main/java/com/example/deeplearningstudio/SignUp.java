package com.example.deeplearningstudio;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    String signupUrl = getResources().getString(R.string.server_url)+"signup";

    EditText name, email, password;
    TextView signInText;
    Button butt, gooButt;

    Intent intent;

    InputValidatorHelper inputValidatorHelper = new InputValidatorHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = (EditText) findViewById(R.id.signUpName);
        email = (EditText) findViewById(R.id.signUpEmail);
        password = (EditText) findViewById(R.id.signUpPassword);
        butt = (Button) findViewById(R.id.signUpButton);
        gooButt = (Button) findViewById(R.id.signUpGoogle);
        signInText = (TextView) findViewById(R.id.signInText);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputValidatorHelper.isNullOrEmpty(name.getText().toString())) {
                    name.setError("Name can not be empty");
                    butt.setEnabled(false);
                }else if(inputValidatorHelper.isValidName(name.getText().toString())){
                    name.setError("Name is not valid");
                    butt.setEnabled(false);
                }
                else {
                    butt.setEnabled(true);
                }

            }
        });

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
                    post_dict.put("name", name.getText().toString());
                    post_dict.put("email", email.getText().toString());
                    post_dict.put("password", password.getText().toString());

                    HttpPostRequest postReq = new HttpPostRequest();

                    String response = postReq.execute(signupUrl, String.valueOf(post_dict)).get();
                    System.out.println("Sign Up Response: " + response);
                    JSONObject obj = new JSONObject(response);
                    System.out.println(obj.getString("text").replace("\\", ""));

                    if (obj.getString("text").equals("Username already exists")) {
                        email.setError("This email is already registered");
                    }
                    else if (obj.getString("text").equals("Logged in")){
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                    }else if (obj.getString("text").equals("OK")){
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }

}
