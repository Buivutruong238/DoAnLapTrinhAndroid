package com.nhom08.doanlaptrinhandroid.BLL;

import com.nhom08.doanlaptrinhandroid.DAL.UserLikePostDAL;
import com.nhom08.doanlaptrinhandroid.DTO.UserLikePost;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;

public class UserLikePostBLL {
    private UserLikePostDAL userLikePostDAL;

    public void getLike(String strAPI, OnMyFinishListener<Integer> onMyFinishListener){
        userLikePostDAL = new UserLikePostDAL();
        userLikePostDAL.getLike(strAPI, onMyFinishListener);
    }

    public void addOrUpdateUserLikePost(String strAPI, UserLikePost userLikePost, OnMyFinishListener<Boolean> onMyFinishListener){
        userLikePostDAL = new UserLikePostDAL();
        userLikePostDAL.addOrUpdateUserLikePost(strAPI, userLikePost, onMyFinishListener);
    }

    public void getSumLike(String strAPI, OnMyFinishListener<Integer> onMyFinishListener){
        userLikePostDAL = new UserLikePostDAL();
        userLikePostDAL.getSumLike(strAPI, onMyFinishListener);
    }
}
