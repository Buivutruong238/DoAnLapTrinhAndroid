package com.nhom08.doanlaptrinhandroid.DAL;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Wp_user_DAL {
    public Wp_user_DAL(){}

    public void toArrayWp_users(String strUrl, OnMyFinishListener<ArrayList<Wp_user>> onMyFinishListener){
        class Tmp extends TaskBackground<ArrayList<Wp_user>>{

            public Tmp(OnMyFinishListener<ArrayList<Wp_user>> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected ArrayList<Wp_user> doInBackground_Something(String... params) {
                ArrayList<Wp_user> list = new ArrayList<>();
                JSONObject jsonObject;
                try {
                    jsonObject = FunctionsStatic.getJsonObjectFromInternet(params[0], FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL);
                    JSONArray jsonArray = jsonObject.getJSONArray("wp_users");
                    Wp_user wp_user;
                    JSONObject obj;
                    for (int i = 0; i< jsonArray.length(); i++){
                        obj = jsonArray.getJSONObject(i);
                        wp_user = new Wp_user();
                        wp_user.setID(obj.getInt("ID"));
                        wp_user.setUser_login(obj.getString("user_login"));
                        wp_user.setUser_pass(obj.getString("user_pass"));
                        wp_user.setUser_nicename(obj.getString("user_nicename"));
                        wp_user.setUser_email(obj.getString("user_email"));
                        wp_user.setUser_url(obj.getString("user_url"));
                        wp_user.setUser_registered(obj.getString("user_registered"));
                        wp_user.setUser_activation_key(obj.getString("user_activation_key"));
                        wp_user.setUser_status(obj.getInt("user_status"));
                        wp_user.setDisplay_name(obj.getString("display_name"));

                        list.add(wp_user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setBonusOfCoder("Error to parse jsonObject!");
                    return null;
                }

                return list;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }

    public void addUser(String strUrl, final Wp_user user, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                try {
                    //Check bad connect
                    URL url = new URL(params[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("user_login", user.getUser_login()));
                    nameValuePairs.add(new BasicNameValuePair("user_pass", FunctionsStatic.hashMD5(user.getUser_pass())));
                    nameValuePairs.add(new BasicNameValuePair("user_email", user.getUser_email()));
                    nameValuePairs.add(new BasicNameValuePair("user_registered", user.getUser_registered()));
                    nameValuePairs.add(new BasicNameValuePair("display_name", user.getDisplay_name()));

                    //Insert
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(params[0]);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse httpResponse = client.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();

                    //Listening Response From Server
                    InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();

                    bufferedReader.close();
                    inputStreamReader.close();

                    int num = Integer.parseInt(line);
                    if(num == 1)
                        return true;
                    return false;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    this.setBonusOfCoder("Url not found from internet!");
                } catch (IOException e) {
                    e.printStackTrace();
                    this.setBonusOfCoder("Fail to connect to url");
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }

    public void updatePassWord(String strUrl, final Wp_user newUserPass, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                try {
                    //check bad connection.
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    httpURLConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //params http post.
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("user_email", newUserPass.getUser_email()));
                    nameValuePairs.add(new BasicNameValuePair("user_pass", newUserPass.getUser_pass()));

                    //update entity.
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(params[0]);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse httpResponse = client.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();

                    //Listening response from server.
                    InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    int num = Integer.parseInt(line);

                    //return success.
                    return num == 1;

                } catch (MalformedURLException e) {
                    this.setBonusOfCoder("Url Not Found!");
                    e.printStackTrace();
                } catch (IOException e) {
                    this.setBonusOfCoder("Could't Connect Url!");
                    e.printStackTrace();
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }
}