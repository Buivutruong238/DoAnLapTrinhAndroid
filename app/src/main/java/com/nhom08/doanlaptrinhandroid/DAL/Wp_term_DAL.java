package com.nhom08.doanlaptrinhandroid.DAL;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_term;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_term;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.Modulds.TaskBackground;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Wp_term_DAL {
    public Wp_term_DAL(){}

    public void toArrayWpTerms(String strUrl, OnMyFinishListener<ArrayList<Wp_term>> onMyFinishListener){
        class Tmp extends TaskBackground<ArrayList<Wp_term>>{

            public Tmp(OnMyFinishListener<ArrayList<Wp_term>> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected ArrayList<Wp_term> doInBackground_Something(String... params) {
                ArrayList<Wp_term> list = new ArrayList<>();

                try{
                    JSONObject jsonObject = FunctionsStatic.getJsonObjectFromInternet(params[0], FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL);
                    JSONArray jsonArray = jsonObject.getJSONArray("wp_terms");
                    JSONObject obj;
                    Wp_term wp_term;
                    for(int i = 0; i < jsonArray.length(); i++){
                        obj = jsonArray.getJSONObject(i);
                        wp_term = new Wp_term();
                        wp_term.setTerm_id(obj.getInt("term_id"));
                        wp_term.setName(obj.getString("name"));
                        list.add(wp_term);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

                return list;
            }
        }
        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }
}