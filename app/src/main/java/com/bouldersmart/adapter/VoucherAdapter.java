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

import com.bouldersmart.BoulderSmartApplication;
import com.bouldersmart.R;
import com.bouldersmart.model.VoucherDataModel;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<VoucherDataModel> listApproach;
    private BoulderSmartApplication application;
    private VoucherInterface anInterface;

    public interface VoucherInterface {
        void voucherPurchased(String identify, int position);
    }

    public VoucherAdapter(Activity activity, ArrayList<VoucherDataModel> listApproach,
                          VoucherInterface anInterface) {
        this.activity = activity;
        this.listApproach = listApproach;
        this.anInterface = anInterface;
        application = (BoulderSmartApplication) activity.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vouchers_list,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        VoucherDataModel result = listApproach.get(position);
        holder.tvVoucherName.setText(result.getVoucher_name());
        holder.tvVoucherDiscount.setText(result.getDiscount() + " % " + activity.getResources()
                .getString(R.string.off));
        holder.tvVoucherPoints.setText(result.getPoints());
        holder.tvVoucherCode.setText(result.getVoucher_code());

        if (result.getIs_purchase().equals("1")) {
            holder.tvVoucherCode.setVisibility(View.VISIBLE);
            holder.llVoucherPoints.setVisibility(View.GONE);
        } else {
            holder.tvVoucherCode.setVisibility(View.GONE);
            holder.llVoucherPoints.setVisibility(View.VISIBLE);
        }
        try {
            application.getmImageLoader().displayImage(result.getVoucher_image(), holder.ivVoucherImage,
                    application.getDisplayImageOptionForBackground(activity.getResources().getDrawable(
                            R.drawable.ic_gallery_32dp)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.llVoucherPoints.setOnClickListener(v -> {
            if (anInterface != null) {
                anInterface.voucherPurchased("purchase", position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listApproach.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvVoucherName, tvVoucherDiscount, tvVoucherPoints, tvVoucherCode;
        ImageView ivVoucherImage;
        LinearLayout llVoucherPoints;

        MyViewHolder(View view) {
            super(view);
            tvVoucherName = view.findViewById(R.id.tvVoucherName);
            tvVoucherDiscount = view.findViewById(R.id.tvVoucherDiscount);
            tvVoucherPoints = view.findViewById(R.id.tvVoucherPoints);
            tvVoucherCode = view.findViewById(R.id.tvVoucherCode);
            ivVoucherImage = view.findViewById(R.id.ivVoucherImage);
            llVoucherPoints = view.findViewById(R.id.llVoucherPoints);
        }
    }
}
