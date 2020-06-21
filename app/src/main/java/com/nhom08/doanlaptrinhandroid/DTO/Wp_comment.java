package com.nhom08.doanlaptrinhandroid.DTO;

public class Wp_comment {
    private int comment_ID;
    private int  comment_post_ID;
    private String comment_author;
    private String  comment_author_email;
    private String comment_date;
    private String  comment_content;
    private String comment_author_url;
    private int user_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getComment_author_url() {
        return comment_author_url;
    }

    public void setComment_author_url(String comment_author_url) {
        this.comment_author_url = comment_author_url;
    }

    public int getComment_ID() {
        return comment_ID;
    }

    public void setComment_ID(int comment_ID) {
        this.comment_ID = comment_ID;
    }

    public int getComment_post_ID() {
        return comment_post_ID;
    }

    public void setComment_post_ID(int comment_post_ID) {
        this.comment_post_ID = comment_post_ID;
    }

    public String getComment_author() {
        return comment_author;
    }

    public void setComment_author(String comment_author) {
        this.comment_author = comment_author;
    }

    public String getComment_author_email() {
        return comment_author_email;
    }

    public void setComment_author_email(String comment_author_email) {
        this.comment_author_email = comment_author_email;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    @Override
    public String toString() {
        return "Wp_comment{" +
                "comment_ID=" + comment_ID +
                ", comment_post_ID=" + comment_post_ID +
                ", comment_author='" + comment_author + '\'' +
                ", comment_author_email='" + comment_author_email + '\'' +
                ", comment_date='" + comment_date + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", comment_author_url='" + comment_author_url + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}
