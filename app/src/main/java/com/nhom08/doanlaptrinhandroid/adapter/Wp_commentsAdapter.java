package com.nhom08.doanlaptrinhandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.R;

import java.util.ArrayList;

public class Wp_commentsAdapter extends BaseAdapter {
    private ArrayList<Wp_comment> comments;
    private LayoutInflater inflater;

    public Wp_commentsAdapter(Context context, ArrayList<Wp_comment> comments){
        this.comments = comments;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getComment_ID();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_item_comment_of_user, parent, false);
        TextView tvValueTime = convertView.findViewById(R.id.tvValueThoiGianItemComemntOfUser),
                tvValueTitle = convertView.findViewById(R.id.tvValuePostNameItemCommentOfUser),
                tvNoiDung = convertView.findViewById(R.id.tvNoiDungBinhLuanItemCommentOfUser);

        Wp_comment comment = comments.get(position);
        tvValueTime.setText(comment.getComment_date());
        tvValueTitle.setText("PostID: "+comment.getComment_post_ID());
        tvNoiDung.setText(comment.getComment_content());

        return convertView;
    }
}
