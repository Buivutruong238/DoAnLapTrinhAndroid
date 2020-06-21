package com.nhom08.doanlaptrinhandroid.DTO;

public class Wp_post {
    private int ID;
    private int post_author;
    private String post_date;
    private String post_content;
    private String post_title;
    private String post_name;
    private String post_modified;
    private String guid;
    private int comment_count;

    public String getPost_name() {
        return post_name;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPost_author() {
        return post_author;
    }

    public void setPost_author(int post_author) {
        this.post_author = post_author;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_modified() {
        return post_modified;
    }

    public void setPost_modified(String post_modified) {
        this.post_modified = post_modified;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public Wp_post(){}

    @Override
    public String toString() {
        return "Wp_post{" +
                "ID=" + ID +
                ", post_author=" + post_author +
                ", post_date='" + post_date + '\'' +
                ", post_content='" + post_content + '\'' +
                ", post_title='" + post_title + '\'' +
                ", post_name='" + post_name + '\'' +
                ", post_modified='" + post_modified + '\'' +
                ", guid='" + guid + '\'' +
                ", comment_count=" + comment_count +
                '}';
    }
}