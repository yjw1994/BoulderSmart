package com.bouldersmart.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.model.CommnetDataModel;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<CommnetDataModel> listContacts;

    public CommentAdapter(Activity activity, ArrayList<CommnetDataModel> listContacts) {
        this.activity = activity;
        this.listContacts = listContacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        CommnetDataModel result = listContacts.get(position);
        holder.tvComment.setText(result.getComments());

        holder.tvNameComment.setText(result.getFname() + " " + result.getLname());
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvComment, tvNameComment;
        LinearLayout llCommentView;

        MyViewHolder(View view) {
            super(view);
            tvComment = view.findViewById(R.id.tvComment);
            tvNameComment = view.findViewById(R.id.tvNameComment);
            llCommentView = view.findViewById(R.id.llCommentView);
        }
    }
}
