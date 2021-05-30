package com.example.deeplearningstudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoViewAttacher;

import org.jetbrains.annotations.NotNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisualizationFragment extends Fragment {

    String projectID;
    Spinner X_column, Y_column;
    Button plot;
    ImageView plot_img;
    PhotoViewAttacher photo_attacher;
    String plot_type = "bar";

    public VisualizationFragment(String projID) {
        projectID = projID;
    }

    public static void newInstance(String param1, String param2) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visualization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout BG = (LinearLayout) getView().findViewById(R.id.barGraphLayout);
        LinearLayout SP = (LinearLayout) getView().findViewById(R.id.scatterPlotLayout);
        LinearLayout LG = (LinearLayout) getView().findViewById(R.id.lineGraphLayout);
        TextView bgText = (TextView) getView().findViewById(R.id.bgText);
        TextView spText = (TextView) getView().findViewById(R.id.spText);
        TextView lgText = (TextView) getView().findViewById(R.id.lgText);
        View bgLine = (View) getView().findViewById(R.id.bgLine);
        View spLine = (View) getView().findViewById(R.id.spLine);
        View lgLine = (View) getView().findViewById(R.id.lgLine);
        plot = (Button) view.findViewById(R.id.plot_butt);

        X_column = (Spinner) view.findViewById(R.id.x_column_spinner);
        Y_column = (Spinner) view.findViewById(R.id.y_column_spinner);
        plot_img = (ImageView) view.findViewById(R.id.plot_img);
        photo_attacher = new PhotoViewAttacher(plot_img);

        HttpRequest req = new HttpRequest();
        String url = getResources().getString(R.string.server_url) + "getColumns?project=" + projectID;
        req.execute(url);

        plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data_to_send = new JSONObject();
                try {
                    String x = X_column.getSelectedItem().toString();
                    String y = Y_column.getSelectedItem().toString();
                    if (x.equals("Select Column") || y.equals("Select Column")) {
                        Toast.makeText(getContext(), "Select the columnss before plotting", Toast.LENGTH_LONG).show();
                    } else {
                        data_to_send.put("x", x);
                        data_to_send.put("y", y);
                        data_to_send.put("plottype", plot_type);
                        System.out.println("Plot Data: " + data_to_send);
                        PlotGraph req = new PlotGraph();
                        String url = getResources().getString(R.string.server_url) + "plotData";
                        req.execute(url, String.valueOf(data_to_send));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        BG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_type = "bar";
                bgLine.setBackgroundColor(getResources().getColor(R.color.primary_color));
                bgText.setTextColor(getResources().getColor(R.color.primary_color));

                spLine.setBackgroundColor(getResources().getColor(R.color.black));
                spText.setTextColor(getResources().getColor(R.color.black));

                lgLine.setBackgroundColor(getResources().getColor(R.color.black));
                lgText.setTextColor(getResources().getColor(R.color.black));

            }
        });

        SP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_type = "scatter";
                spLine.setBackgroundColor(getResources().getColor(R.color.primary_color));
                spText.setTextColor(getResources().getColor(R.color.primary_color));

                bgLine.setBackgroundColor(getResources().getColor(R.color.black));
                bgText.setTextColor(getResources().getColor(R.color.black));

                lgLine.setBackgroundColor(getResources().getColor(R.color.black));
                lgText.setTextColor(getResources().getColor(R.color.black));

            }
        });

        LG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_type = "line";
                lgLine.setBackgroundColor(getResources().getColor(R.color.primary_color));
                lgText.setTextColor(getResources().getColor(R.color.primary_color));

                spLine.setBackgroundColor(getResources().getColor(R.color.black));
                spText.setTextColor(getResources().getColor(R.color.black));

                bgLine.setBackgroundColor(getResources().getColor(R.color.black));
                bgText.setTextColor(getResources().getColor(R.color.black));

            }
        });

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
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("In Background: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            System.out.println("Columns List Response: " + response);
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray columns = obj.getJSONArray("columns");
                ArrayList<String> columns_list = new ArrayList<String>();
                columns_list.add("Select Column");
                for (int i = 0; i < columns.length(); i++) {
                    columns_list.add(columns.getString(i));
                }
                System.out.println("List: " + columns_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, columns_list);

                X_column.setAdapter(adapter);
                Y_column.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(response);
        }
    }

    private class PlotGraph extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 60000;
        static final int CONNECTION_TIMEOUT = 60000;
        static final String COOKIES_HEADER = "Set-Cookie";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Plotting",
                    "Plotting graph. Please wait...", true);
            //dialog.setCancelable(false);
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
            dialog.dismiss();
            System.out.println("Plot Data Response: " + response);
            try {
                String image_url = getResources().getString(R.string.server_url) + new JSONObject(response).getString("imagePath");
                System.out.println("Image URL = " + image_url);
                GetGraph plt_graph = new GetGraph();
                plt_graph.execute(image_url);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetGraph extends AsyncTask<String, Void, Bitmap> {
        static final int READ_TIMEOUT = 60000;
        static final int CONNECTION_TIMEOUT = 60000;
        static final String COOKIES_HEADER = "Set-Cookie";

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bmp = null;
            try {
                SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = pref.edit();
                System.out.println(params[0]);
                // connect to the server
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //System.out.println("Received Cookie from PREF: "+pref.getString("Cookie", ""));
                connection.setRequestProperty("Cookie",
                        pref.getString("Cookies", ""));
                bmp = BitmapFactory.decodeStream(connection.getInputStream());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            plot_img.setImageBitmap(bmp);
        }
    }

}