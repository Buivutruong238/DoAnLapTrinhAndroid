package com.nhom08.doanlaptrinhandroid.DAL;

import android.util.Log;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyUpdateProgress;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.Modulds.TaskBackground;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wp_post_DAL {

    public Wp_post_DAL(){}

    public void toArrayWpPosts(final String url, OnMyFinishListener<ArrayList<Wp_post>> onMyFinishListener){
        class Tmp extends TaskBackground<ArrayList<Wp_post>>{

            public Tmp(OnMyFinishListener<ArrayList<Wp_post>> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected ArrayList<Wp_post> doInBackground_Something(String... params) {
                ArrayList<Wp_post> list = new ArrayList<>();
                String str = FunctionsStatic.getStringFromInternet(url);
                try {
                    JSONObject obj = new JSONObject(str);
                    JSONArray array = obj.getJSONArray("wp_post");
                    JSONObject object;
                    Wp_post post;
                    for (int i = 0; i< array.length(); i++){
                        object = array.getJSONObject(i);
                        post = new Wp_post();
                        post.setID(object.getInt("ID"));
                        post.setPost_author(object.getInt("post_author"));
                        post.setPost_date(object.getString("post_date"));
                        post.setPost_content(object.getString("post_content"));
                        post.setPost_title(object.getString("post_title"));
                        post.setPost_modified(object.getString("post_modified"));

                        post.setGuid(object.getString("guid"));
                        list.add(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return list;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public void findItems(final ArrayList<Wp_post> wp_posts_source, final String keySearch, final int percent_math, OnMyFinishListener<ArrayList<Wp_post>> onMyFinishListener, final OnMyUpdateProgress<Integer, Integer> onMyUpdateProgress){
        class Tmp extends TaskBackground<ArrayList<Wp_post>>{

            public Tmp(OnMyFinishListener<ArrayList<Wp_post>> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected ArrayList<Wp_post> doInBackground_Something(String... params) {
                //convert keySearch to english.
                String key = FunctionsStatic.VNCharacterToEnglishCharacter(keySearch);
                ArrayList<Wp_post> destination = new ArrayList<>();

                if (percent_math == 100) {
                    for (int i = 0 ; i< wp_posts_source.size(); i++) {
                        Wp_post post = wp_posts_source.get(i);
                        onMyUpdateProgress.onUpdateProgress(i);
                        //merge title and content and to english character.
                        String lock = FunctionsStatic.VNCharacterToEnglishCharacter(post.getPost_content() + post.getPost_title());
                        if (lock.toLowerCase().contains(key)) {
                            destination.add(post);
                            onMyUpdateProgress.onUpdateValue(destination.size());
                        }
                    }
                    if (destination.size() == 0)
                        getPostWithPercent_math(key, destination);
                }else{
                    getPostWithPercent_math(key, destination);
                }

                return destination;
            }

            private void getPostWithPercent_math(String key, ArrayList<Wp_post> destination){
                //Distribute key to array.
                String[] keys = key.split(" ");
                //convert per_math to numberCount.
                int numberCount = keys.length * percent_math / 100;
//                for(String s : keys)
//                    Log.i("myResult", s);
//                Log.i("myResult", "-> "+numberCount +" / "+percent_math);
                int count;
                for(int i = 0; i < wp_posts_source.size(); i++){
                    Wp_post post = wp_posts_source.get(i);
                    onMyUpdateProgress.onUpdateProgress(i);
                    count = 0;
                    //merge title and content and to english character.
                    String lock = FunctionsStatic.VNCharacterToEnglishCharacter(post.getPost_content() + post.getPost_title());
//                    Log.i("myResult", "--------------------------------");
                    ArrayList<Integer> distantOfEachWord = new ArrayList<>();
                    for(String s : keys){
                        String l = lock.toLowerCase();
                        s = s.toLowerCase();
                        while (true){
                            if (l.contains(s)) {
                                count++;
                                int id = l.indexOf(s);
                                distantOfEachWord.add(id);
                                l = l.substring(id + s.length()-1);
                            }
                            else
                                break;
                        }
                    }
                    Collections.sort(distantOfEachWord);
//                    Log.i("myResult", "count: "+count);
                    if (count >= numberCount) {
                        //check distant before added.
                        boolean isAdded = true;
                        if (distantOfEachWord.size() > 1){
                            for(int j = 0; j < distantOfEachWord.size() - 1; j ++){
                                int distant = Math.abs(distantOfEachWord.get(j) - distantOfEachWord.get(j+1));
//                                Log.i("myResult", "distant: "+distant+"| of i: "+distantOfEachWord.get(j)+" |j: "+distantOfEachWord.get(j+1));
                                if (distant > key.length()) {
                                    isAdded = false;
                                    break;
                                }
                                if (j==keys.length-1)
                                    break;
                            }
//                            Log.i("myResult", "leng: "+key.length());
                        }

                        if (isAdded){
                            destination.add(post);
                            onMyUpdateProgress.onUpdateValue(destination.size());
                        }
                    }
                }
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public void addWpPost(final String strUrl, final Wp_post wp_post, final OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                //Check bad connect
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("post_author", String.valueOf(wp_post.getPost_author())));
                    nameValuePairs.add(new BasicNameValuePair("post_date", wp_post.getPost_date()));
                    nameValuePairs.add(new BasicNameValuePair("post_modified", wp_post.getPost_modified()));
                    nameValuePairs.add(new BasicNameValuePair("post_content", wp_post.getPost_content()));
                    nameValuePairs.add(new BasicNameValuePair("post_title", wp_post.getPost_title()));
                    nameValuePairs.add(new BasicNameValuePair("post_name", wp_post.getPost_name()));

                    //Insert
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(strUrl);
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
                    if(num > 0)
                        return true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public void updateGuid(final String strUrl, final int ID, final String host, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                //Check bad connect
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("ID", String.valueOf(ID)));
                    nameValuePairs.add(new BasicNameValuePair("Host", host));

                    //Insert
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(strUrl);
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
                    if(num > 0)
                        return true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public void insertTermRelationship(final String strUrl, final int object_id, final int term_id, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                //Check bad connect
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("object_id", String.valueOf(object_id)));
                    nameValuePairs.add(new BasicNameValuePair("term_taxonomy_id", String.valueOf(term_id)));

                    //Insert
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(strUrl);
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
                    if(num > 0)
                        return true;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }
        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public void deleteWpPost(final String strUrl, final int ID, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                //Check bad connect
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("ID", String.valueOf(ID)));

                    //Insert
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(strUrl);
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
                    if(num > 0)
                        return true;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }
}