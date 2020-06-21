package com.nhom08.doanlaptrinhandroid.Modulds;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;

public class PostAndProgressBarAndTextView {
    private Wp_post post;
    private ProgressBar progressBar;
    private TextView tvSoLuongLike;

    public TextView getTvSoLuongLike() {
        return tvSoLuongLike;
    }

    public void setTvSoLuongLike(TextView tvSoLuongLike) {
        this.tvSoLuongLike = tvSoLuongLike;
    }

    public Wp_post getPost() {
        return post;
    }

    public void setPost(Wp_post post) {
        this.post = post;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public PostAndProgressBarAndTextView(Wp_post post, ProgressBar progressBar, TextView tvSoLuongLike) {
        this.post = post;
        this.progressBar = progressBar;
        this.tvSoLuongLike = tvSoLuongLike;
    }
}
