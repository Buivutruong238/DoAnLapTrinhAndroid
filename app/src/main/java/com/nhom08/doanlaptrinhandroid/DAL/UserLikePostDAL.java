package com.nhom08.doanlaptrinhandroid.DAL;

import android.annotation.SuppressLint;

import com.nhom08.doanlaptrinhandroid.DTO.UserLikePost;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.Modulds.TaskBackground;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserLikePostDAL {
    public void getLike(String strAPI, OnMyFinishListener<Integer> onMyFinishListener){
        class Tmp extends TaskBackground<Integer>{

            public Tmp(OnMyFinishListener<Integer> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Integer doInBackground_Something(String... params) {
                String strAPI = params[0];
                int like = 0;
                try {
                    JSONObject jsonObject = FunctionsStatic.getJsonObjectFromInternet(strAPI);
                    JSONArray jsonArray = jsonObject.getJSONArray("likes");
                    if (jsonArray.length() == 0)
                        return -2;//user chua like
                    else{
                        like = Integer.parseInt(jsonArray.getJSONObject(0).getString("like"));
                    }
                } catch (JSONException | NumberFormatException e) {
                    setBonusOfCoder(e.getMessage());
                }

                return like;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strAPI);
    }

    public void addOrUpdateUserLikePost(String strAPI, final UserLikePost userLikePost, OnMyFinishListener<Boolean> onMyFinishListener){
        @SuppressLint("StaticFieldLeak")
        class Tmp extends TaskBackground<Boolean> {

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                boolean result;

                try {
                    //check bad url
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    httpURLConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //NameValuePair
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("post_id", String.valueOf(userLikePost.getPost_id())));
                    nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(userLikePost.getUser_id())));
                    nameValuePairs.add(new BasicNameValuePair("like", String.valueOf(userLikePost.getLike())));

                    //Post Data To Server
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(params[0]);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();

                    //read response from server
                    InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String line = bufferedReader.readLine();

                    int response = Integer.parseInt(line);
                    result = response > 0;

                    bufferedReader.close();
                    inputStreamReader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                }

                return result;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strAPI);
    }

    public void getSumLike(String strAPI, OnMyFinishListener<Integer> onMyFinishListener){
        class Tmp extends TaskBackground<Integer>{

            private Tmp(OnMyFinishListener<Integer> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Integer doInBackground_Something(String... params) {
                String strAPI = params[0];
                int like = 0;
                try {
                    JSONObject jsonObject = FunctionsStatic.getJsonObjectFromInternet(strAPI);
                    JSONArray jsonArray = jsonObject.getJSONArray("sums");
                    if (jsonArray.length() == 0)
                        return -2;//user chua like
                    else{
                        like = Integer.parseInt(jsonArray.getJSONObject(0).getString("sum"));
                    }
                } catch (JSONException | NumberFormatException e) {
                    setBonusOfCoder(e.getMessage());
                }

                return like;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strAPI);
    }
}