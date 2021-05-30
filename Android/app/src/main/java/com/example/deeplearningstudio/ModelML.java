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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

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

public class ModelML extends Fragment {

    String projectID;
    private LinearLayout classification_text_layout, regression_text_layout, clustering_text_layout;
    private LinearLayout classification_param_layout, regression_param_layout, clustering_param_layout,
            decisionTreeLayout, randomForestLayout, logisticRegressionLayout,
            linearRegressionLayout, decisionTreeRegressorLayout, regressionRandomForestLayout,
            kMeansLayout;
    private TextView classification_text, regression_text, clustering_text, training_percent, testing_percent, training_logs_view;
    private View classification_line, regression_line, clustering_line;
    private Spinner classification_algo, regressionAlgo, clusteringAlgo, column_name;
    private SeekBar seekBar;
    private Button train_butt, export_model;

    private EditText random_forest_class_trees, logistic_regressor_max_iter, k_means_no_of_clusters, k_means_max_iter;
    private Spinner decision_tree_class_criterion, decision_tree_class_splitter, random_forest_class_criterion, logistic_regressor_penalty,
            logistic_regressor_multiclass, logistic_regressor_solver, linear_regressor_normalize, linear_regressor_fit_intercept,
            decision_tree_regress_criterion, decision_tree_regress_splitter, k_means_init_points, k_means_algorithm;

    String algo_type = "Classification";

    public ModelML(String projID) {
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
        return inflater.inflate(R.layout.fragment_model_ml, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        training_percent = (TextView) view.findViewById(R.id.trainingPercent);
        testing_percent = (TextView) view.findViewById(R.id.testingPercent);
        export_model = (Button) view.findViewById(R.id.export_model);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        column_name = (Spinner) view.findViewById(R.id.column_name);
        decision_tree_class_criterion = (Spinner) view.findViewById(R.id.decision_tree_criterion);
        decision_tree_class_splitter = (Spinner) view.findViewById(R.id.decision_tree_splitter);
        random_forest_class_trees = (EditText) view.findViewById(R.id.noOfTress);
        random_forest_class_criterion = (Spinner) view.findViewById(R.id.random_forest_criterion);
        logistic_regressor_penalty = (Spinner) view.findViewById(R.id.penalty);
        logistic_regressor_multiclass = (Spinner) view.findViewById(R.id.multiClass);
        logistic_regressor_solver = (Spinner) view.findViewById(R.id.solver);
        logistic_regressor_max_iter = (EditText) view.findViewById(R.id.maxIterations);
        linear_regressor_normalize = (Spinner) view.findViewById(R.id.linearRegNormalize);
        linear_regressor_fit_intercept = (Spinner) view.findViewById(R.id.linear_fitIntercept);
        decision_tree_regress_criterion = (Spinner) view.findViewById(R.id.regressorCriterion);
        decision_tree_regress_splitter = (Spinner) view.findViewById(R.id.regressorSplitter);
        k_means_no_of_clusters = (EditText) view.findViewById(R.id.noOfClusters);
        k_means_init_points = (Spinner) view.findViewById(R.id.initialPoints);
        k_means_max_iter = (EditText) view.findViewById(R.id.kMeansMaxIterations);
        k_means_algorithm = (Spinner) view.findViewById(R.id.algorithm);

        training_logs_view = (TextView) view.findViewById(R.id.training_logs_view);
        train_butt = (Button) view.findViewById(R.id.train_butt);
        classification_text_layout = (LinearLayout) view.findViewById(R.id.clasLayout);
        regression_text_layout = (LinearLayout) view.findViewById(R.id.regLayout);
        clustering_text_layout = (LinearLayout) view.findViewById(R.id.cluLayout);
        classification_text = (TextView) getView().findViewById(R.id.clasText);
        regression_text = (TextView) getView().findViewById(R.id.regText);
        clustering_text = (TextView) getView().findViewById(R.id.cluText);
        classification_line = (View) getView().findViewById(R.id.clasLine);
        regression_line = (View) getView().findViewById(R.id.regLine);
        clustering_line = (View) getView().findViewById(R.id.cluLine);

        classification_param_layout = (LinearLayout) view.findViewById(R.id.classificationLayout);
        regression_param_layout = (LinearLayout) view.findViewById(R.id.regressionLayout);
        clustering_param_layout = (LinearLayout) view.findViewById(R.id.clusteringLayout);

        decisionTreeLayout = (LinearLayout) view.findViewById(R.id.decisionTreeLayout);
        randomForestLayout = (LinearLayout) view.findViewById(R.id.randomForestLayout);
        logisticRegressionLayout = (LinearLayout) view.findViewById(R.id.logisticRegressionLayout);

        linearRegressionLayout = (LinearLayout) view.findViewById(R.id.linearRegressionLayout);
        decisionTreeRegressorLayout = (LinearLayout) view.findViewById(R.id.decisionTreeRegressorLayout);
        regressionRandomForestLayout = (LinearLayout) view.findViewById(R.id.regressionRandomForestLayout);

        kMeansLayout = (LinearLayout) view.findViewById(R.id.kMeansLayout);

        classification_algo = (Spinner) view.findViewById(R.id.classificationAlgo);
        regressionAlgo = (Spinner) view.findViewById(R.id.regressionAlgo);
        clusteringAlgo = (Spinner) view.findViewById(R.id.clusteringAlgo);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                testing_percent.setText("" + progress + "%");
                training_percent.setText("" + (100 - progress) + "%");
                if (progress < 50) {
                    training_percent.setText("50%");
                    seekBar.setProgress(50);
                } else if (progress > 90) {
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

        classification_text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classification_line.setBackgroundColor(getResources().getColor(R.color.primary_color));
                classification_text.setTextColor(getResources().getColor(R.color.primary_color));

                regression_line.setBackgroundColor(getResources().getColor(R.color.black));
                regression_text.setTextColor(getResources().getColor(R.color.black));

                clustering_line.setBackgroundColor(getResources().getColor(R.color.black));
                clustering_text.setTextColor(getResources().getColor(R.color.black));

                classification_param_layout.setVisibility(View.VISIBLE);
                regression_param_layout.setVisibility(View.GONE);
                clustering_param_layout.setVisibility(View.GONE);
                algo_type = "Classification";

            }
        });

        classification_algo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String class_algo = classification_algo.getSelectedItem().toString();
                if (class_algo.equals("Gaussian Naive Bayes")) {
                    decisionTreeLayout.setVisibility(View.GONE);
                    randomForestLayout.setVisibility(View.GONE);
                    logisticRegressionLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Multinomial Naive Bayes")) {
                    decisionTreeLayout.setVisibility(View.GONE);
                    randomForestLayout.setVisibility(View.GONE);
                    logisticRegressionLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Bernoulli Naive Bayes")) {
                    decisionTreeLayout.setVisibility(View.GONE);
                    randomForestLayout.setVisibility(View.GONE);
                    logisticRegressionLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Decision Tree Classifier")) {
                    decisionTreeLayout.setVisibility(View.VISIBLE);
                    randomForestLayout.setVisibility(View.GONE);
                    logisticRegressionLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Random Forest")) {
                    decisionTreeLayout.setVisibility(View.GONE);
                    randomForestLayout.setVisibility(View.VISIBLE);
                    logisticRegressionLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Logistic Regression")) {
                    decisionTreeLayout.setVisibility(View.GONE);
                    randomForestLayout.setVisibility(View.GONE);
                    logisticRegressionLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regression_text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regression_line.setBackgroundColor(getResources().getColor(R.color.primary_color));
                regression_text.setTextColor(getResources().getColor(R.color.primary_color));

                classification_line.setBackgroundColor(getResources().getColor(R.color.black));
                classification_text.setTextColor(getResources().getColor(R.color.black));

                clustering_line.setBackgroundColor(getResources().getColor(R.color.black));
                clustering_text.setTextColor(getResources().getColor(R.color.black));

                classification_param_layout.setVisibility(View.GONE);
                regression_param_layout.setVisibility(View.VISIBLE);
                clustering_param_layout.setVisibility(View.GONE);

                algo_type = "Regression";
            }
        });

        regressionAlgo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String class_algo = regressionAlgo.getSelectedItem().toString();
                if (class_algo.equals("Linear Regression")) {
                    linearRegressionLayout.setVisibility(View.VISIBLE);
                    decisionTreeRegressorLayout.setVisibility(View.GONE);
                    regressionRandomForestLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Decision Tree Regressor")) {
                    linearRegressionLayout.setVisibility(View.GONE);
                    decisionTreeRegressorLayout.setVisibility(View.VISIBLE);
                    regressionRandomForestLayout.setVisibility(View.GONE);
                } else if (class_algo.equals("Random Forest")) {
                    linearRegressionLayout.setVisibility(View.GONE);
                    decisionTreeRegressorLayout.setVisibility(View.GONE);
                    regressionRandomForestLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clustering_text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clustering_line.setBackgroundColor(getResources().getColor(R.color.primary_color));
                clustering_text.setTextColor(getResources().getColor(R.color.primary_color));

                classification_line.setBackgroundColor(getResources().getColor(R.color.black));
                classification_text.setTextColor(getResources().getColor(R.color.black));

                regression_line.setBackgroundColor(getResources().getColor(R.color.black));
                regression_text.setTextColor(getResources().getColor(R.color.black));

                classification_param_layout.setVisibility(View.GONE);
                regression_param_layout.setVisibility(View.GONE);
                clustering_param_layout.setVisibility(View.VISIBLE);

                algo_type = "Clustering";

            }
        });

        clusteringAlgo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String class_algo = clusteringAlgo.getSelectedItem().toString();
                if (class_algo.equals("K-means")) {
                    kMeansLayout.setVisibility(View.VISIBLE);
                } else if (class_algo.equals("Hierarchical Clustering")) {
                    kMeansLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        train_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data_to_send = new JSONObject();
                if (algo_type.equals("Classification")) {
                    String algorithm = classification_algo.getSelectedItem().toString();
                    JSONObject parameters = new JSONObject();
                    try {
                        if (algorithm.equals("Decision Tree Classifier")) {
                            parameters.put("criterion", decision_tree_class_criterion.getSelectedItem().toString());
                            parameters.put("splitter", decision_tree_class_splitter.getSelectedItem().toString());
                        } else if (algorithm.equals("Random Forest")) {
                            parameters.put("criterion", random_forest_class_criterion.getSelectedItem().toString());
                            parameters.put("trees", random_forest_class_trees.getText().toString());
                        } else if (algorithm.equals("Logistic Regression")) {
                            parameters.put("penalty", logistic_regressor_penalty.getSelectedItem().toString());
                            parameters.put("max_iter", logistic_regressor_max_iter.getText().toString());
                            parameters.put("multi_class", logistic_regressor_multiclass.getSelectedItem().toString());
                            parameters.put("solver", logistic_regressor_solver.getSelectedItem().toString());
                        }
                        parameters.put("output_coulmn", column_name.getSelectedItem().toString());
                        parameters.put("validation_split", seekBar.getProgress());
                        data_to_send.put("algo_type", algo_type);
                        data_to_send.put("algorithm", algorithm);
                        data_to_send.put("parameters", parameters);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (algo_type.equals("Regression")) {
                    String algorithm = regressionAlgo.getSelectedItem().toString();
                    JSONObject parameters = new JSONObject();
                    try {
                        if (algorithm.equals("Linear Regression")) {
                            Boolean normalize = linear_regressor_normalize.getSelectedItem().toString().equals("True");
                            Boolean fit_intercept = linear_regressor_fit_intercept.getSelectedItem().toString().equals("True");
                            parameters.put("normalize", normalize);
                            parameters.put("fit_intercept", fit_intercept);
                        } else if (algorithm.equals("Decision Tree Regressor")){
                            parameters.put("criterion", decision_tree_regress_criterion.getSelectedItem().toString());
                            parameters.put("splitter", decision_tree_regress_splitter.getSelectedItem().toString());
                        }
                        parameters.put("output_coulmn", column_name.getSelectedItem().toString());
                        parameters.put("validation_split", seekBar.getProgress());
                        data_to_send.put("algo_type", algo_type);
                        data_to_send.put("algorithm", algorithm);
                        data_to_send.put("parameters", parameters);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (algo_type.equals("Clustering")){
                    String algorithm = clusteringAlgo.getSelectedItem().toString();
                    JSONObject parameters = new JSONObject();
                    try {
                        parameters.put("clusters", k_means_no_of_clusters.getText().toString());
                        parameters.put("initial_points", k_means_init_points.getSelectedItem().toString());
                        parameters.put("algorithm", k_means_algorithm.getSelectedItem().toString());
                        parameters.put("max_iter", k_means_max_iter.getText().toString());
                        parameters.put("validation_split", seekBar.getProgress());
                        data_to_send.put("algo_type", algo_type);
                        data_to_send.put("algorithm", algorithm);
                        data_to_send.put("parameters", parameters);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TrainModel req = new TrainModel();
                String url = getResources().getString(R.string.server_url) + "generatemodelml?project=" + projectID;
                req.execute(url, String.valueOf(data_to_send));
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

        GetColumns req = new GetColumns();
        String url = getResources().getString(R.string.server_url) + "getColumns?project=" + projectID;
        req.execute(url);

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(response);
        }
    }

    private class TrainModel extends AsyncTask<String, Void, String> {
        static final String COOKIES_HEADER = "Set-Cookie";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            train_butt.setEnabled(false);
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
            train_butt.setEnabled(true);
            export_model.setVisibility(View.VISIBLE);
            System.out.println(response);
            try {
                JSONObject resp = new JSONObject(response);
                training_logs_view.setText(resp.getString("data") + "\nTraining Complete");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            super.onPostExecute(response);
            try {
                JSONObject resp = new JSONObject(response);
                String code = resp.getString("data");
                ExportModelDialog exp_dialog = new ExportModelDialog(getActivity(), code);
                exp_dialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            setContentView(R.layout.export_model_ml);

            TextView codeview = (TextView) findViewById(R.id.code);
            codeview.setText(code);

        }
    }
}