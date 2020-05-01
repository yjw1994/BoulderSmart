package com.bouldersmart.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.bouldersmart.model.RouteDataModel;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<RouteDataModel> listRoute;

    public RoutesAdapter(Activity activity, ArrayList<RouteDataModel> listRoute) {
        this.activity = activity;
        this.listRoute = listRoute;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_route_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        RouteDataModel result = listRoute.get(position);
        holder.tvRouteName.setText(result.getRoute_name());
        if (result.getRatting() != null && result.getRatting().length() != 0) {
            holder.srbRouteList.setRating(Float.parseFloat(result.getRatting()));
        }
    }

    @Override
    public int getItemCount() {
        return listRoute.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRouteName;
        ScaleRatingBar srbRouteList;

        MyViewHolder(View view) {
            super(view);
            tvRouteName = view.findViewById(R.id.tvRouteName);
            srbRouteList = view.findViewById(R.id.srbRouteList);
        }
    }
}
