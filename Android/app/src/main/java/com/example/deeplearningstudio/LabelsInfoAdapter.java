package com.example.deeplearningstudio;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

public class LabelsInfoAdapter extends RecyclerView.Adapter<LabelsInfoAdapter.ViewHolder>{

    JSONArray labels_data;

    public LabelsInfoAdapter(JSONArray label_data){
        labels_data = label_data;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.col_labels_info, parent, false);
        LabelsInfoAdapter.ViewHolder viewHolder = new LabelsInfoAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        try {
            JSONArray temp = labels_data.getJSONArray(position);
            Log.d("Labels Data", temp.getString(0));
            holder.label_name.setText(temp.getString(0));
            holder.label_count.setText(temp.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return labels_data.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label_name;
        TextView label_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label_name = itemView.findViewById(R.id.label_name);
            label_count = itemView.findViewById(R.id.label_count);
        }
    }
}
