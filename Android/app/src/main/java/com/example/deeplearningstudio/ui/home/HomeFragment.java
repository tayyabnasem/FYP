package com.example.deeplearningstudio.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeplearningstudio.AdopterListView;
import com.example.deeplearningstudio.BottomNavigationMenu;
import com.example.deeplearningstudio.HttpGetRequest;
import com.example.deeplearningstudio.HttpPostRequest;
import com.example.deeplearningstudio.NewProjectScreen;
import com.example.deeplearningstudio.ProjectDescriptionFragment;
import com.example.deeplearningstudio.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
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

public class HomeFragment extends Fragment {
    String projectsURL = null;

    RecyclerView recyclerView;
    AdopterListView adapter;
    FloatingActionButton floatingActionButton;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
//recycler
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerListProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HttpRequest getReq = new HttpRequest();

        projectsURL = getResources().getString(R.string.server_url)+"getProjects";
        getReq.execute(projectsURL);
        floatingActionButton= root.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), NewProjectScreen.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpRequest getReq = new HttpRequest();
        projectsURL = getResources().getString(R.string.server_url)+"getProjects";
        getReq.execute(projectsURL);
    }

    public class HttpRequest extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";


        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String inputLine;
            try {
                SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                System.out.println(params[0]);
                // connect to the server
                URL myUrl = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //System.out.println("Received Cookie from PREF: "+pref.getString("Cookie", ""));
                connection.setRequestProperty("Cookie",
                        pref.getString("Cookies", ""));

                connection.setRequestMethod("GET");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
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
                    System.out.println("Length of Cookies Header: "+cookiesHeader.size());
                    for (String cookie : cookiesHeader) {
                        System.out.println("Individual Cookie: "+ HttpCookie.parse(cookie).get(0).toString());
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
            try {
                System.out.println("Project Response " + response);
                JSONArray respArr = new JSONArray(response);
                adapter = new AdopterListView(respArr);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
}