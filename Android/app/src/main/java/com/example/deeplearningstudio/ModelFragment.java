package com.example.deeplearningstudio;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ModelFragment extends Fragment {

    String projectID;
    RecyclerView recyclerView;
    ModelLayersAdapter recyclerAdapter;
    EditText epoch, learning_rate;
    Spinner batch_size, loss_function, optimizer, categorize_output, column_name;
    Button train_butt, new_layer_butt, save_model_butt, save_hyperparameters_butt, export_model;
    TextView training_logs_view, train_accuracy_val, test_accuracy_val, no_layers_text, training_percent, testing_percent;
    LinearLayout results_layout;
    ImageView accuracy_img, loss_img;
    SeekBar seekBar;

    private Socket mSocket;


    ArrayList<JSONObject> layers = new ArrayList<JSONObject>();

    public ModelFragment(String projID) {
        projectID = projID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_model, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        epoch = (EditText) view.findViewById(R.id.epoch);
        learning_rate = (EditText) view.findViewById(R.id.learning_rate);
        batch_size = (Spinner) view.findViewById(R.id.batch_size);
        loss_function = (Spinner) view.findViewById(R.id.loss_function);
        optimizer = (Spinner) view.findViewById(R.id.optimizer);
        categorize_output = (Spinner) view.findViewById(R.id.categorize_output);
        column_name = (Spinner) view.findViewById(R.id.column_name);
        train_butt = (Button) view.findViewById(R.id.train_butt);
        new_layer_butt = (Button) view.findViewById(R.id.new_layer_butt);
        training_logs_view = (TextView) view.findViewById(R.id.training_logs_view);
        results_layout = (LinearLayout) view.findViewById(R.id.results_layout);
        accuracy_img = (ImageView) view.findViewById(R.id.plot_img_accuracy);
        loss_img = (ImageView) view.findViewById(R.id.plot_img_loss);
        test_accuracy_val = (TextView) view.findViewById(R.id.test_accuracy_val);
        train_accuracy_val = (TextView) view.findViewById(R.id.train_accuracy_val);
        save_model_butt = (Button) view.findViewById(R.id.save_changes_layers);
        save_hyperparameters_butt = (Button) view.findViewById(R.id.save_changes_hyperparamter);
        no_layers_text = (TextView)view.findViewById(R.id.no_layers_text);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        training_percent = (TextView) view.findViewById(R.id.trainingPercent);
        testing_percent = (TextView) view.findViewById(R.id.testingPercent);
        export_model = (Button) view.findViewById(R.id.export_model);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                testing_percent.setText("" + progress + "%");
                training_percent.setText("" + (100 - progress) + "%");
                if(progress < 50){
                    training_percent.setText("50%");
                    seekBar.setProgress(50);
                }
                else if(progress > 90){
                    seekBar.setProgress(90);
                    testing_percent.setText("90%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        save_model_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray data_to_send = null;
                data_to_send = new JSONArray(recyclerAdapter.layers);
                String url = getResources().getString(R.string.server_url) + "saveModel?project=" + projectID;
                SaveModel req = new SaveModel();
                req.execute(url, String.valueOf(data_to_send));

            }
        });

        save_hyperparameters_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject hyperparameters = new JSONObject();
                try {
                    hyperparameters.put("epoch", epoch.getText().toString());
                    hyperparameters.put("learningRate", learning_rate.getText().toString());
                    hyperparameters.put("batchSize", batch_size.getSelectedItem().toString());
                    hyperparameters.put("lossFunction", loss_function.getSelectedItem().toString());
                    hyperparameters.put("optimizer", optimizer.getSelectedItem().toString());
                    hyperparameters.put("categorize_output", categorize_output.getSelectedItem().toString());
                    hyperparameters.put("output_coulmn", column_name.getSelectedItem().toString());
                    hyperparameters.put("validation_split", seekBar.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = getResources().getString(R.string.server_url) + "saveHyperparameter?project=" + projectID;
                SaveHyperparameters req = new SaveHyperparameters();
                req.execute(url, String.valueOf(hyperparameters));
            }
        });


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        train_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data_to_send = new JSONObject();
                if (recyclerAdapter.layers.isEmpty()){
                    Toast.makeText(getActivity(), "No Layer exists in the model to train", Toast.LENGTH_SHORT).show();
                }else if (column_name.getSelectedItem().toString().equals("Select Column")){
                    Toast.makeText(getActivity(), "You must select the Output column before training", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        data_to_send.put("layers", new JSONArray(recyclerAdapter.layers));
                        JSONObject hyperparameters = new JSONObject();
                        hyperparameters.put("epoch", epoch.getText().toString());
                        hyperparameters.put("learningRate", learning_rate.getText().toString());
                        hyperparameters.put("batchSize", batch_size.getSelectedItem().toString());
                        hyperparameters.put("lossFunction", loss_function.getSelectedItem().toString());
                        hyperparameters.put("optimizer", optimizer.getSelectedItem().toString());
                        hyperparameters.put("categorize_output", categorize_output.getSelectedItem().toString());
                        hyperparameters.put("output_coulmn", column_name.getSelectedItem().toString());
                        hyperparameters.put("validation_split", seekBar.getProgress());
                        data_to_send.put("hyperparameters", hyperparameters);

                        String url = getResources().getString(R.string.server_url) + "generatemodel?project=" + projectID;
                        TrainModel req = new TrainModel();
                        train_butt.setEnabled(false);
                        req.execute(url, String.valueOf(data_to_send));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        new_layer_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewLayerDialog dialog = new NewLayerDialog(getActivity());
                dialog.show();
            }
        });

        export_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCode req = new GetCode();
                String url = getResources().getString(R.string.server_url)+"getModelCode?project=" + projectID;
                req.execute(url);
            }
        });

        GetColumns request = new GetColumns();
        String url = getResources().getString(R.string.server_url) + "getColumns?project=" + projectID;
        request.execute(url);
    }


    private class ModelLayersAdapter extends RecyclerView.Adapter<ModelLayersAdapter.ViewHolder> {
        ArrayList<JSONObject> layers;

        public ModelLayersAdapter(ArrayList<JSONObject> layers) {
            this.layers = layers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.model_swapable_items, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                String layer_name = layers.get(position).getString("layerName");
                holder.textView.setText(layer_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return layers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imageView;
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.layerName);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                JSONObject layer_data = layers.get(getAdapterPosition());
                EditLayerDialog dialog = new EditLayerDialog(getActivity(), layer_data, getAdapterPosition());
                dialog.show();
            }
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
            System.out.println("Project Response " + response);
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray layer_temp = obj.getJSONObject("data").getJSONArray("layers");
                for (int i = 0; i < layer_temp.length(); i++) {
                    layers.add(layer_temp.getJSONObject(i));
                }
                recyclerAdapter = new ModelLayersAdapter(layers);
                recyclerView.setAdapter(recyclerAdapter);
                if (layers.isEmpty()){
                    no_layers_text.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else{
                    JSONObject hyperparameters = obj.getJSONObject("data").getJSONObject("hyperparameters");
                    epoch.setText(Integer.toString(hyperparameters.getInt("epoch")));
                    learning_rate.setText(Double.toString(hyperparameters.getDouble("learningRate")));
                    batch_size.setSelection(((ArrayAdapter) batch_size.getAdapter()).getPosition(hyperparameters.getString("batchSize")));
                    loss_function.setSelection(((ArrayAdapter) loss_function.getAdapter()).getPosition(hyperparameters.getString("lossFunction")));
                    optimizer.setSelection(((ArrayAdapter) optimizer.getAdapter()).getPosition(hyperparameters.getString("optimizer")));
                    categorize_output.setSelection(((ArrayAdapter) categorize_output.getAdapter()).getPosition(hyperparameters.getString("categorize_output")));
                    seekBar.setProgress(hyperparameters.getInt("validation_split"));
                    column_name.setSelection(((ArrayAdapter) column_name.getAdapter()).getPosition(hyperparameters.getString("output_coulmn")));

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                            int fromPosition = viewHolder.getAdapterPosition();
                            int toPosition = target.getAdapterPosition();
                            Collections.swap(recyclerAdapter.layers, fromPosition, toPosition);
                            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        }
                    });
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetColumns extends AsyncTask<String, Void, String> {
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

                column_name.setAdapter(adapter);
                HttpRequest req = new HttpRequest();
                String url = getResources().getString(R.string.server_url) + "getModelInfo?project=" + projectID;
                req.execute(url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(response);
        }
    }

    private class TrainModel extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 60000;
        static final int CONNECTION_TIMEOUT = 60000;
        static final String COOKIES_HEADER = "Set-Cookie";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            training_logs_view.setText("Training...");
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
                connection.setRequestProperty("Connection", "Keep-Alive");
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
            training_logs_view.setText(training_logs_view.getText().toString()+"\nTraining Complete");
            export_model.setVisibility(View.VISIBLE);
            train_butt.setEnabled(true);
            try {
                JSONObject obj = new JSONObject(response);
                String accuracy_img_url = obj.getJSONObject("data").getString("model_accuracy_img");
                String loss_img_url = obj.getJSONObject("data").getString("model_loss_img");
                String test_accuracy =obj.getJSONObject("data").getString("model_test_accuracy");
                String train_accuracy =obj.getJSONObject("data").getString("model_accuracy");
                results_layout.setVisibility(View.VISIBLE);

                train_accuracy_val.setText(train_accuracy_val.getText().toString()+train_accuracy);
                test_accuracy_val.setText(test_accuracy_val.getText().toString()+test_accuracy);
                Ion.with(accuracy_img)
                        .error(R.drawable.app_logo)
                        .load(getResources().getString(R.string.server_url)+accuracy_img_url);
                Ion.with(loss_img)
                        .error(R.drawable.app_logo)
                        .load(getResources().getString(R.string.server_url)+loss_img_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(response);
        }
    }

    private class SaveModel extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getContext(), "Saving Model",
                    "Saving Model. Please wait...", true);
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
            dialog.dismiss();
            System.out.println(response);
        }
    }

    private class SaveHyperparameters extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getContext(), "Saving Model",
                    "Saving Model. Please wait...", true);
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
            dialog.dismiss();
            System.out.println(response);
        }
    }

    private class GetCode extends AsyncTask<String, Void, String> {
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;
        static final String COOKIES_HEADER = "Set-Cookie";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Getting Code",
                    "Fetching Model Code. Please wait...", true);
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
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            System.out.println("Project Code: " + response);
            dialog.dismiss();
            try {
                JSONObject resp = new JSONObject(response);
                String code = resp.getString("data");
                ExportModelDialog exp_dialog = new ExportModelDialog(getActivity(), code);
                exp_dialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(response);
        }
    }

    private class NewLayerDialog extends Dialog {
        private Spinner layer_name, dense_activation, lstm_activation, lstm_return_sequence, lstm_recurrent_activation;
        private LinearLayout dense_layer_layout, dropout_layer_layout, lstm_layer_layout;
        private Button add_button, cancel_button;
        private EditText dense_units, dropout_dropout_rate, lstm_units, lstm_dropout_rate;

        public NewLayerDialog(Activity a) {
            super(a);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.deep_learning_model_new_layer);

            dense_layer_layout = (LinearLayout) findViewById(R.id.denseLayout);
            dropout_layer_layout = (LinearLayout) findViewById(R.id.dropoutLayout);
            lstm_layer_layout = (LinearLayout) findViewById(R.id.lstmLayout);
            add_button = (Button) findViewById(R.id.add_button);
            cancel_button = (Button) findViewById(R.id.cancel_button);
            dense_units = (EditText) findViewById(R.id.inputDenseUnits);
            dense_activation = (Spinner) findViewById(R.id.spinnerDenseActivationFunction);
            dropout_dropout_rate = (EditText) findViewById(R.id.inputDropoutRate);
            lstm_activation = (Spinner) findViewById(R.id.spinnerLSTMActivation);
            lstm_units = (EditText) findViewById(R.id.inputLSTMUnits);
            lstm_return_sequence = (Spinner) findViewById(R.id.spinnerReturnSequence);
            lstm_dropout_rate = (EditText) findViewById(R.id.inputLSTMDropout);
            lstm_recurrent_activation = (Spinner) findViewById(R.id.spinnerLSTM_RAF);

            layer_name = (Spinner) findViewById(R.id.spinnerLayerName);

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String layer = layer_name.getSelectedItem().toString();
                    JSONObject new_layer_params = new JSONObject();
                    if (layer.equals("Dense")) {
                        try {
                            new_layer_params.put("layerName", "Dense");
                            new_layer_params.put("units", Integer.parseInt(dense_units.getText().toString()));
                            new_layer_params.put("activationFunction", dense_activation.getSelectedItem().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (layer.equals("Dropout")) {
                        try {
                            new_layer_params.put("layerName", "Dropout");
                            new_layer_params.put("dropoutRate", Double.parseDouble(dropout_dropout_rate.getText().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            new_layer_params.put("layerName", "LSTM");
                            new_layer_params.put("units", Integer.parseInt(lstm_units.getText().toString()));
                            new_layer_params.put("activationFunction", lstm_activation.getSelectedItem().toString());
                            new_layer_params.put("recurrentActivation", lstm_recurrent_activation.getSelectedItem().toString());
                            new_layer_params.put("dropout", Double.parseDouble(lstm_dropout_rate.getText().toString()));
                            new_layer_params.put("returnSequence", lstm_return_sequence.getSelectedItem().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    no_layers_text.setVisibility(View.GONE);
                    recyclerAdapter.layers.add(new_layer_params);
                    recyclerAdapter.notifyDataSetChanged();
                    dismiss();
                }
            });

            layer_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String layer = layer_name.getSelectedItem().toString();
                    if (layer.equals("Dense")) {
                        dense_layer_layout.setVisibility(View.VISIBLE);
                        dropout_layer_layout.setVisibility(View.GONE);
                        lstm_layer_layout.setVisibility(View.GONE);
                    } else if (layer.equals("Dropout")) {
                        dense_layer_layout.setVisibility(View.GONE);
                        dropout_layer_layout.setVisibility(View.VISIBLE);
                        lstm_layer_layout.setVisibility(View.GONE);
                    } else {
                        dense_layer_layout.setVisibility(View.GONE);
                        dropout_layer_layout.setVisibility(View.GONE);
                        lstm_layer_layout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    private class EditLayerDialog extends Dialog {

        TextView layer_name;
        private Spinner dense_activation, lstm_activation, lstm_return_sequence, lstm_recurrent_activation;
        private LinearLayout dense_layer_layout, dropout_layer_layout, lstm_layer_layout;
        private Button remove_button, cancel_button;
        private EditText dense_units, dropout_dropout_rate, lstm_units, lstm_dropout_rate;
        JSONObject layer_data;
        int index;

        EditLayerDialog(Activity activity, JSONObject layer_data, int index){
            super(activity);
            this.layer_data = layer_data;
            this.index = index;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.deep_learning_edit_model_layer);
            layer_name = (TextView) findViewById(R.id.layer_name);
            dense_layer_layout = (LinearLayout) findViewById(R.id.denseLayout);
            dropout_layer_layout = (LinearLayout) findViewById(R.id.dropoutLayout);
            lstm_layer_layout = (LinearLayout) findViewById(R.id.lstmLayout);
            remove_button = (Button) findViewById(R.id.remove_button);
            cancel_button = (Button) findViewById(R.id.cancel_button);
            dense_units = (EditText) findViewById(R.id.inputDenseUnits);
            dense_activation = (Spinner) findViewById(R.id.spinnerDenseActivationFunction);
            dropout_dropout_rate = (EditText) findViewById(R.id.inputDropoutRate);
            lstm_activation = (Spinner) findViewById(R.id.spinnerLSTMActivation);
            lstm_units = (EditText) findViewById(R.id.inputLSTMUnits);
            lstm_return_sequence = (Spinner) findViewById(R.id.spinnerReturnSequence);
            lstm_dropout_rate = (EditText) findViewById(R.id.inputLSTMDropout);
            lstm_recurrent_activation = (Spinner) findViewById(R.id.spinnerLSTM_RAF);

            try {
                String layer = layer_data.getString("layerName");
                if (layer.equals("Dense")) {
                    dense_layer_layout.setVisibility(View.VISIBLE);
                    dropout_layer_layout.setVisibility(View.GONE);
                    lstm_layer_layout.setVisibility(View.GONE);
                    dense_units.setText(layer_data.getString("units"));
                    dense_activation.setSelection(((ArrayAdapter) dense_activation.getAdapter()).getPosition(layer_data.getString("activationFunction")));
                } else if (layer.equals("Dropout")) {
                    dense_layer_layout.setVisibility(View.GONE);
                    dropout_layer_layout.setVisibility(View.VISIBLE);
                    lstm_layer_layout.setVisibility(View.GONE);
                    dropout_dropout_rate.setText(layer_data.getString("dropoutRate"));
                } else {
                    dense_layer_layout.setVisibility(View.GONE);
                    dropout_layer_layout.setVisibility(View.GONE);
                    lstm_layer_layout.setVisibility(View.VISIBLE);
                    lstm_units.setText(layer_data.getString("units"));
                    lstm_activation.setSelection(((ArrayAdapter) lstm_activation.getAdapter()).getPosition(layer_data.getString("activationFunction")));
                    lstm_return_sequence.setSelection(((ArrayAdapter) lstm_return_sequence.getAdapter()).getPosition(layer_data.getString("returnSequence")));
                    lstm_dropout_rate.setText(layer_data.getString("dropout"));
                    lstm_recurrent_activation.setSelection(((ArrayAdapter) lstm_recurrent_activation.getAdapter()).getPosition(layer_data.getString("recurrentActivation")));
                }
                layer_name.setText(layer);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerAdapter.layers.remove(index);
                    recyclerAdapter.notifyDataSetChanged();
                    dismiss();
                }
            });
        }
    }

    private class ExportModelDialog extends Dialog {

        public Activity c;
        private String code;

        public ExportModelDialog(Activity a, String c) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            code = c;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.export_model);

            LinearLayout code_layout = (LinearLayout) findViewById(R.id.code_layout);
            LinearLayout tm_layout = (LinearLayout) findViewById(R.id.tm_layout);
            LinearLayout input_field = (LinearLayout) findViewById(R.id.input_field);

            TextView codeText = (TextView) findViewById(R.id.codeText);
            TextView tmText = (TextView) findViewById(R.id.tmText);
            TextView codeview = (TextView) findViewById(R.id.code);
            codeview.setText(code);

            View codeLine = (View) findViewById(R.id.codeLine);
            View tmLine = (View) findViewById(R.id.tmLine);

            Button butt = (Button) findViewById(R.id.download_butt);

            code_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeLine.setBackgroundColor(getResources().getColor(R.color.primary_color));
                    codeText.setTextColor(getResources().getColor(R.color.primary_color));

                    tmLine.setBackgroundColor(getResources().getColor(R.color.black));
                    tmText.setTextColor(getResources().getColor(R.color.black));

                    butt.setVisibility(View.GONE);
                    input_field.setVisibility(View.VISIBLE);
                }
            });

            tm_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tmLine.setBackgroundColor(getResources().getColor(R.color.primary_color));
                    tmText.setTextColor(getResources().getColor(R.color.primary_color));

                    codeLine.setBackgroundColor(getResources().getColor(R.color.black));
                    codeText.setTextColor(getResources().getColor(R.color.black));

                    butt.setVisibility(View.VISIBLE);
                    input_field.setVisibility(View.GONE);
                }
            });

        }
    }
}