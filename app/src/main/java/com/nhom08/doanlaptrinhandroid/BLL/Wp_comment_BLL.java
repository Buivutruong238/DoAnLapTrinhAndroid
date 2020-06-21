package com.nhom08.doanlaptrinhandroid.BLL;

import com.nhom08.doanlaptrinhandroid.DAL.Wp_comment_DAL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;

import java.util.ArrayList;

public class Wp_comment_BLL {
    private Wp_comment_DAL wp_comment_dal;

    public Wp_comment_BLL(){}

    public void toArrayWp_comment(String strUrl, OnMyFinishListener<ArrayList<Wp_comment>> onMyFinishListener){
        wp_comment_dal = new Wp_comment_DAL();
        wp_comment_dal.toArrayWp_comment(strUrl, onMyFinishListener);
    }

    public void addComment(String strUrl, Wp_comment wp_comment, OnMyFinishListener<Boolean> onMyFinishListener){
        //Check data before insert to server

        wp_comment_dal = new Wp_comment_DAL();
        wp_comment_dal.addComment(strUrl, wp_comment, onMyFinishListener);
    }

    public void deleteWpComment(final String strUrl,final int comment_ID, OnMyFinishListener<Boolean> onMyFinishListener){
        wp_comment_dal = new Wp_comment_DAL();
        wp_comment_dal.deleteWpComment(strUrl, comment_ID, onMyFinishListener);
    }

    public void countCommentOfPost(String strUrl, OnMyFinishListener<Integer> onMyFinishListener) {
        wp_comment_dal = new Wp_comment_DAL();
        wp_comment_dal.countCommentOfPost(strUrl, onMyFinishListener);
    }
}
