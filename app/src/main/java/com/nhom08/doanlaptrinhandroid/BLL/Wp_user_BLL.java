package com.nhom08.doanlaptrinhandroid.BLL;

import com.nhom08.doanlaptrinhandroid.DAL.Wp_user_DAL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;

import java.util.ArrayList;

public class Wp_user_BLL {
    private Wp_user_DAL wp_user_dal;

    public Wp_user_BLL(){ }

    public void toArrayWp_users(String strUrl, OnMyFinishListener<ArrayList<Wp_user>> onMyFinishListener){
        wp_user_dal = new Wp_user_DAL();
        wp_user_dal.toArrayWp_users(strUrl, onMyFinishListener);
    }

    public void addUser(String strUrl, Wp_user user, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_user_dal = new Wp_user_DAL();
        wp_user_dal.addUser(strUrl, user, onMyFinishListener);
    }

    public void updatePassWord(String strUrl, Wp_user newUserPass, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_user_dal = new Wp_user_DAL();
        wp_user_dal.updatePassWord(strUrl, newUserPass, onMyFinishListener);
    }
}
