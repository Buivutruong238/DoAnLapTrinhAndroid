package com.nhom08.doanlaptrinhandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.R;

import java.util.ArrayList;
import java.util.List;

public class Wp_postRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Wp_post> wp_posts;

    public Wp_postRecyclerViewAdapter(List<Wp_post> wp_posts){
        this.wp_posts = wp_posts;
    }

    public static class VH extends RecyclerView.ViewHolder{

        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wp_post_item_recyclerview, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return wp_posts.size();
    }
}
