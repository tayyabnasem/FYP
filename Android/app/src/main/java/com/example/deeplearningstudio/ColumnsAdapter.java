package com.example.deeplearningstudio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ColumnsAdapter extends RecyclerView.Adapter<ColumnsAdapter.ViewHolder> {
    public JSONArray columnsData;
    private Context mContext;
    public JSONObject model;

    public ColumnsAdapter(Context context, JSONArray data_statistics, JSONObject preproc_data) {
        mContext = context;
        columnsData = data_statistics;
        model = new JSONObject();
        if (preproc_data.length() != 0) {
            model = preproc_data;
            System.out.println(model);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.preprocessing_columns, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONArray temp = null;
        try {
            String col_name = columnsData.getJSONObject(position).getString("name");
            holder.colName.setText(col_name);
            holder.include_column.setChecked(model.getJSONObject(col_name).getBoolean("include"));
            holder.include_column.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        model.getJSONObject(col_name).put("include", isChecked);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final CustomDialogClass cdd = new CustomDialogClass((Activity) mContext, columnsData.getJSONObject(position));
                        cdd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                try {
                                    if (columnsData.getJSONObject(position).getString("type").equals("Numerical")) {
                                        model.getJSONObject(col_name).put("impute_int_with", cdd.impute_int_with_spinner.getSelectedItem().toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        cdd.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return columnsData.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView colName;
        LinearLayout cardView;
        Switch include_column;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.colName = itemView.findViewById(R.id.columnName);
            include_column = itemView.findViewById(R.id.include_switch);
            cardView = (LinearLayout) itemView.findViewById(R.id.cardView);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class CustomDialogClass extends Dialog {

        public Activity c;
        private JSONObject col_data;
        public Spinner impute_int_with_spinner;
        EditText impute_str_with;

        public CustomDialogClass(Activity a, JSONObject columnData) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            col_data = columnData;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_column_info_popup);
            impute_int_with_spinner = (Spinner) findViewById(R.id.spinner_input_int);
            impute_str_with = (EditText) findViewById(R.id.edit_input_str);
            RecyclerView labels_info = (RecyclerView) findViewById(R.id.recyclerLabelInfoList);
            LinearLayout integer_col_stats = (LinearLayout) findViewById(R.id.integer_col_stats);
            LinearLayout labels_info_stats = (LinearLayout) findViewById(R.id.RecyclerLayout);
            try {
                if (col_data.getString("type").equals("Numerical")) {
                    labels_info_stats.setVisibility(View.GONE);
                    integer_col_stats.setVisibility(View.VISIBLE);
                    impute_int_with_spinner.setVisibility(View.VISIBLE);
                    impute_str_with.setVisibility(View.GONE);
                    TextView col_min = (TextView) findViewById(R.id.col_min);
                    TextView col_max = (TextView) findViewById(R.id.col_max);
                    TextView col_mean = (TextView) findViewById(R.id.col_mean);
                    TextView col_std_dev = (TextView) findViewById(R.id.col_stddev);
                    col_min.setText(col_data.getString("min"));
                    col_max.setText(col_data.getString("max"));
                    col_mean.setText(col_data.getString("mean"));
                    col_std_dev.setText(col_data.getString("std"));
                } else {
                    labels_info_stats.setVisibility(View.VISIBLE);
                    integer_col_stats.setVisibility(View.GONE);
                    impute_int_with_spinner.setVisibility(View.GONE);
                    impute_str_with.setVisibility(View.VISIBLE);
                    labels_info.setLayoutManager(new LinearLayoutManager(getContext()));
                    LabelsInfoAdapter adapter = null;
                    try {
                        adapter = new LabelsInfoAdapter(col_data.getJSONArray("labels"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    labels_info.setAdapter(adapter);
                }

                TextView col_name = (TextView) findViewById(R.id.column_name);
                TextView col_type = (TextView) findViewById(R.id.col_type);
                TextView col_missing = (TextView) findViewById(R.id.col_missing);
                TextView col_unique = (TextView) findViewById(R.id.col_unique);


                col_name.setText(col_data.getString("name"));
                col_type.setText(col_data.getString("type"));
                col_missing.setText(col_data.getString("missing"));
                col_unique.setText(col_data.getString("unique"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
