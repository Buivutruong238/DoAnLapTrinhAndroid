package com.nhom08.doanlaptrinhandroid.DAL;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
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
import java.util.List;

public class Wp_comment_DAL {
    public Wp_comment_DAL() {
    }

    public void toArrayWp_comment(String strUrl, OnMyFinishListener<ArrayList<Wp_comment>> onMyFinishListener) {
        class Tmp extends TaskBackground<ArrayList<Wp_comment>> {

            public Tmp(OnMyFinishListener<ArrayList<Wp_comment>> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected ArrayList<Wp_comment> doInBackground_Something(String... params) {
                ArrayList<Wp_comment> list = new ArrayList<>();
                try {
                    JSONObject jsonObject = FunctionsStatic.getJsonObjectFromInternet(params[0]);
                    JSONArray jsonArray = jsonObject.getJSONArray("wp_comments");
                    Wp_comment wp_comment;
                    JSONObject obj;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        obj = jsonArray.getJSONObject(i);
                        wp_comment = new Wp_comment();
                        wp_comment.setComment_ID(obj.getInt("comment_ID"));
                        wp_comment.setComment_post_ID(obj.getInt("comment_post_ID"));
                        wp_comment.setComment_author(obj.getString("comment_author"));
                        wp_comment.setComment_content(obj.getString("comment_content"));
                        wp_comment.setComment_author_email(obj.getString("comment_author_email"));
                        wp_comment.setComment_date(obj.getString("comment_date"));

                        list.add(wp_comment);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    this.setBonusOfCoder("COULD'T TRANSFER TO JSON OBJECT!");
                }

                return list;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }

    public void addComment(String strUrl, final Wp_comment wp_comment, OnMyFinishListener<Boolean> onMyFinishListener) {
        class Tmp extends TaskBackground<Boolean> {

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                Boolean result;

                try {
                    //check bad url
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    httpURLConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //NameValuePair
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("comment_post_ID", String.valueOf(wp_comment.getComment_post_ID())));
                    nameValuePairs.add(new BasicNameValuePair("comment_author", wp_comment.getComment_author()));
                    nameValuePairs.add(new BasicNameValuePair("comment_author_email", wp_comment.getComment_author_email()));
                    nameValuePairs.add(new BasicNameValuePair("comment_author_url", wp_comment.getComment_author_url()));
                    nameValuePairs.add(new BasicNameValuePair("comment_date", wp_comment.getComment_date()));
                    nameValuePairs.add(new BasicNameValuePair("comment_content", wp_comment.getComment_content()));
                    nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(wp_comment.getUser_id())));

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
                    if (response > 0)
                        result = true;
                    else
                        result = false;

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
        tmp.execute(strUrl);
    }

    public void deleteWpComment(final String strUrl, final int comment_ID, OnMyFinishListener<Boolean> onMyFinishListener) {
        class Tmp extends TaskBackground<Boolean> {

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                //Check bad connect
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    urlConnection.setConnectTimeout(FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    //list params
                    //Request: user_login, user_pass, user_email, user_registered, display_name
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("comment_ID", String.valueOf(comment_ID)));

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
                    if (num > 0)
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

    public void countCommentOfPost(String strUrl, OnMyFinishListener<Integer> onMyFinishListener) {
        class Tmp extends TaskBackground<Integer> {

            public Tmp(OnMyFinishListener<Integer> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Integer doInBackground_Something(String... params) {
                String urlAPI = params[0];
                int count = -1;
                try {
                    JSONObject obj = FunctionsStatic.getJsonObjectFromInternet(urlAPI, FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL);
                    JSONArray jsonArray = obj.getJSONArray("wp_comments");
                    count = Integer.parseInt(jsonArray.getJSONObject(0).getString("count(*)"));
                } catch (NumberFormatException | JSONException e) {
                    setBonusOfCoder(e.getMessage());
                }

                return count;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute(strUrl);
    }
}