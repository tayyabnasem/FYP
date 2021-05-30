package com.example.deeplearningstudio;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.HeadersCallback;
import com.koushikdutta.ion.HeadersResponse;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreProcessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreProcessFragment extends Fragment {

    private String projectID, selected_file = null;
    private Button browseFile, uploadFile, filter;
    private ScrollView columns_scroll;
    private CheckBox drop_rows;
    private RecyclerView recyclerView;
    //private ProgressBar upload_progress;
    ColumnsAdapter adapter = null;
    private static final int PICKFILE_RESULT_CODE = 200;

    // TODO: Rename and change types of parameters

    public PreProcessFragment(String projID) {
        // Required empty public constructor
        projectID = projID;
    }

    public static void newInstance() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        browseFile = (Button) view.findViewById(R.id.select);
        uploadFile = (Button) view.findViewById(R.id.upload);
        filter = (Button) view.findViewById(R.id.filter);
        columns_scroll = (ScrollView) view.findViewById(R.id.columns_scroll_view);
        drop_rows = (CheckBox) view.findViewById(R.id.drop_rows);
        //upload_progress = (ProgressBar) view.findViewById(R.id.upload_progress);
        columns_scroll.setVisibility(View.GONE);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpFilterRequest request = new HttpFilterRequest();
                String url = getResources().getString(R.string.server_url) + "filterData?project=" + projectID;
                try {
                    JSONObject obj = new JSONObject();
                    JSONObject over_all_options = new JSONObject();
                    over_all_options.put("drop_rows", drop_rows.isChecked());
                    obj.put("over_all_dataset_options", over_all_options);
                    obj.put("column_wise_options", adapter.model);
                    HttpFilterRequest req = new HttpFilterRequest();
                    req.execute(url, String.valueOf(obj));
                    System.out.println("Data to send: " + obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        browseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 22) {
                    if (checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PermissionChecker.PERMISSION_GRANTED) {
                        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                        String[] mimeTypes = {"text/csv", "text/comma-separated-values"};
                        chooseFile.setType("*/*");
                        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                        startActivityForResult(
                                Intent.createChooser(chooseFile, "Choose a file"),
                                PICKFILE_RESULT_CODE
                        );

                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    System.out.println("SDK <= 22");
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerColumnList);

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_file != null) {
//                    UploadFile uploadFile = new UploadFile();
                    String url = getResources().getString(R.string.server_url) + "uploadFile?plat=1&project=" + projectID;
//                    uploadFile.execute(url, selected_file);
                    Ion.with(getActivity())
                            .load("POST", url).onHeaders(new HeadersCallback() {
                        @Override
                        public void onHeaders(HeadersResponse headers) {

                        }
                    })
//                            .uploadProgressBar(upload_progress)
                            .setMultipartFile("file", new File(selected_file))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e != null) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("ION Response: " + result);
                                    try {
                                        JSONObject parsedData = new JSONObject(result);
                                        if (parsedData.getJSONArray("data").length() != 0) {
                                            columns_scroll.setVisibility(View.VISIBLE);
                                            drop_rows.setChecked(true);
                                            JSONObject preprocessing_opt = new JSONObject();
                                            for (int i = 0; i < parsedData.getJSONArray("data").length(); i++) {
                                                JSONObject column_preproc_opt = new JSONObject();
                                                column_preproc_opt.put("include", true);
                                                column_preproc_opt.put("impute_int_with", "mean");
                                                column_preproc_opt.put("impute_str_with", "Blank");
                                                column_preproc_opt.put("type", parsedData.getJSONArray("data").getJSONObject(i).getString("type"));
                                                column_preproc_opt.put("replacevalue", "");
                                                column_preproc_opt.put("replacewith", "");
                                                preprocessing_opt.put(parsedData.getJSONArray("data").getJSONObject(i).getString("name"), column_preproc_opt);
                                            }
                                            adapter = new ColumnsAdapter(getContext(), parsedData.getJSONArray("data"),
                                                    preprocessing_opt);
                                            recyclerView.setAdapter(adapter);
                                        }

                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                } else {
                    System.out.println("Selected File is null");
                }

            }
        });
        String url = getResources().getString(R.string.server_url) + "getDatasetStaictics?project=" + projectID;

        HttpRequest postReq = new HttpRequest();
        postReq.execute(url);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
            Uri content_describer = data.getData();
            String src = content_describer.getLastPathSegment();
            src = "/storage/emulated/0/" + src.split(":")[1];
            selected_file = src;
            System.out.println("File Selected: " + src);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            System.out.println("Permission: " + permissions[0] + "was " + grantResults[0]);
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimeTypes = {"text/csv", "text/comma-separated-values"};
            chooseFile.setType("*/*");
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    PICKFILE_RESULT_CODE
            );
        }
    }

    private class HttpRequest extends AsyncTask<String, Void, String> {
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
            System.out.println("Data Statistics Response: " + response);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            try {
                JSONObject parsedData = new JSONObject(response);
                if (parsedData.getJSONArray("data_statistics").length() != 0) {
                    columns_scroll.setVisibility(View.VISIBLE);
                    drop_rows.setChecked(parsedData.getJSONObject("preprocessing_options").
                            getJSONObject("over_all_dataset_options").getBoolean("drop_rows"));
                    adapter = new ColumnsAdapter(getContext(), parsedData.getJSONArray("data_statistics"),
                            parsedData.getJSONObject("preprocessing_options").getJSONObject("column_wise_options"));
                    recyclerView.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class HttpFilterRequest extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Filtering",
                    "Applying preprocessing. Please wait...", true);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String inputLine;
            try {
                SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
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
            try {
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("text").equals("Success"))
                    dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}