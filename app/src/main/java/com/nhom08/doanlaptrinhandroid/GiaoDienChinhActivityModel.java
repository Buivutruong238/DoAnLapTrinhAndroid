package com.nhom08.doanlaptrinhandroid;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nhom08.doanlaptrinhandroid.dto.Wp_term;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vietsaclo.android.lib._MyFunctionsStatic;
import vietsaclo.android.lib.interf._MyOnMyFinishListener;

public class GiaoDienChinhActivityModel extends AndroidViewModel {

    private MutableLiveData<String> mutableLiveData;

    public GiaoDienChinhActivityModel(@NonNull Application application) {
        super(application);
        mutableLiveData = new MutableLiveData<>();
    }

    private void setMutableLiveData(String string){
        mutableLiveData.setValue(string);
    }

    MutableLiveData<String> getMutableLiveData(){
        return mutableLiveData;
    }

    void setMutableLiveData_byURL(String strUrl){
        _MyFunctionsStatic.getStringFromInternetTaskBackground(strUrl, new _MyOnMyFinishListener<String>() {
            @Override
            public void onFinish(String result) {
                mutableLiveData.postValue(result);
            }

            @Override
            public void onError(Throwable throwable, Object o) {
                mutableLiveData.postValue(null);
            }
        });
    }

    LiveData<List<Wp_term>> getWp_terms_toList(){
        return Transformations.map(mutableLiveData, new Function<String, List<Wp_term>>() {
            @Override
            public List<Wp_term> apply(String input) {
                try {
                    List<Wp_term> terms = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(input);
                    JSONArray jsonArray = jsonObject.getJSONArray("wp_terms");
                    JSONObject obj;
                    Wp_term term;
                    for (int i= 0;i < jsonArray.length(); i++){
                        obj = jsonArray.getJSONObject(i);
                        term = new Wp_term(
                                obj.getInt("term_id"),
                                obj.getString("name")
                        );
                        terms.add(term);
                    }

                    return terms;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }
}