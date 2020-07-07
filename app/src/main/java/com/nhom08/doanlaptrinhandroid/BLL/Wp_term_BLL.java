package com.nhom08.doanlaptrinhandroid.BLL;

import androidx.annotation.NonNull;

import com.nhom08.doanlaptrinhandroid.DAL.Wp_term_DAL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_term;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;

import java.util.ArrayList;

public class Wp_term_BLL {
    private Wp_term_DAL wp_terms_dal;

    public Wp_term_BLL(){}

    public void toArrayWp_terms(@NonNull String strUrl ,@NonNull OnMyFinishListener<ArrayList<Wp_term>> onMyFinishListener){
        wp_terms_dal = new Wp_term_DAL();
        wp_terms_dal.toArrayWpTerms(strUrl, onMyFinishListener);
    }
}
