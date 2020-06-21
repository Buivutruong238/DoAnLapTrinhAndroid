package com.nhom08.doanlaptrinhandroid.DTO;

public class UserLikePost {
    private int post_id;
    private int user_id;
    private int like;

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "UserLikePost{" +
                "post_id=" + post_id +
                ", user_id=" + user_id +
                ", like=" + like +
                '}';
    }
}
