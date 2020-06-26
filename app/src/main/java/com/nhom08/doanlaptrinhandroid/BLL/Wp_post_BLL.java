package com.nhom08.doanlaptrinhandroid.BLL;

import androidx.annotation.NonNull;

import com.nhom08.doanlaptrinhandroid.DAL.Wp_post_DAL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyUpdateProgress;

import java.util.ArrayList;

public class Wp_post_BLL {
    private Wp_post_DAL wp_post_dal;
    private int secondsTimeOut;

    public Wp_post_BLL(){
        secondsTimeOut = -1;
    }

    public Wp_post_BLL(int secondsTimeOut){
        this.secondsTimeOut = secondsTimeOut;
    }

    public void toArrayWp_posts(@NonNull String strUrl,@NonNull OnMyFinishListener<ArrayList<Wp_post>> finish){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.toArrayWpPosts(strUrl, finish);
    }

    public void findItems(final ArrayList<Wp_post> wp_posts_source, final String keySearch,int percent_math, OnMyFinishListener<ArrayList<Wp_post>> onMyFinishListener, OnMyUpdateProgress<Integer, Integer> onMyUpdateProgress){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.findItems(wp_posts_source, keySearch, percent_math, onMyFinishListener, onMyUpdateProgress);
    }

    public void addWpPost(String strUrl, final Wp_post wp_post, final OnMyFinishListener<Boolean> onMyFinishListener){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.addWpPost(strUrl, wp_post, onMyFinishListener);
    }

    public void updateGuid(final String strUrl, final int ID, String host, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.updateGuid(strUrl, ID, host, onMyFinishListener);
    }

    public void insertTermRelationship(final String strUrl, final int object_id, final int term_id, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.insertTermRelationship(strUrl, object_id, term_id, onMyFinishListener);
    }

    public void deleteWpPost(final String strUrl, final int ID, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_post_dal = new Wp_post_DAL();
        wp_post_dal.deleteWpPost(strUrl, ID, onMyFinishListener);
    }
}