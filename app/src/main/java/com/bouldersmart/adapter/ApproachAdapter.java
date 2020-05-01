package com.bouldersmart.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.ApproachDataModel;

import java.util.ArrayList;

public class ApproachAdapter extends RecyclerView.Adapter<ApproachAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<ApproachDataModel> listApproach;
    ClickListener listener;

    public interface ClickListener {
        void ButtonClicked(String identify, int position);
    }

    public ApproachAdapter(Activity activity, ArrayList<ApproachDataModel> listApproach,
                           ClickListener listener) {
        this.activity = activity;
        this.listApproach = listApproach;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_approach_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ApproachDataModel result = listApproach.get(position);
        holder.tvApproachDate.setText(Utills.dateFormatChange(result.getDate(),
                Constants.INPUT_DATE, Constants.OUTPUT_DATE));
        holder.tvNameAdded.setText(activity.getResources().getString(R.string.submitted_by) + " "
                + result.getFname());

        holder.ivDeleteApproach.setVisibility(View.INVISIBLE);
        if (Preferences.getStringName(Preferences.USER_ID).equals(result.getUser_id())) {
            holder.ivDeleteApproach.setVisibility(View.VISIBLE);
        }

        holder.ivDeleteApproach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.ButtonClicked("delete", position);
                }
            }
        });

        holder.llApproachDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.ButtonClicked("details", position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listApproach.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDeleteApproach;
        TextView tvApproachDate, tvNameAdded;
        LinearLayout llApproachDetails;

        MyViewHolder(View view) {
            super(view);
            tvApproachDate = view.findViewById(R.id.tvApproachDate);
            tvNameAdded = view.findViewById(R.id.tvNameAdded);
            ivDeleteApproach = view.findViewById(R.id.ivDeleteApproach);
            llApproachDetails = view.findViewById(R.id.llApproachDetails);
        }
    }
}
