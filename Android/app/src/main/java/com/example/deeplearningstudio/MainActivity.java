package com.example.deeplearningstudio;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //private static final String SERVER = "http://10.0.2.2:3000/getColumns?project=6082add3e3255e30dcf844f6";
    private static final String SERVER = "http://10.0.2.2:3000/signin";
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            HttpPostRequest postRequest = new HttpPostRequest();
            JSONObject post_dict = new JSONObject();

            try {
                post_dict.put("email" , "tskjabdks");
                post_dict.put("password", "abc");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (post_dict.length() > 0) {
                result = postRequest.execute(SERVER, String.valueOf(post_dict)).get();
                //call to async class
            }
            //result = getRequest.execute(SERVER, ).get();
            System.out.println(result);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}