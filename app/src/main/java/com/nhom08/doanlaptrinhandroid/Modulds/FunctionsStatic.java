package com.nhom08.doanlaptrinhandroid.Modulds;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.R;
import com.nhom08.doanlaptrinhandroid.mail.GMailSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class FunctionsStatic {
    public static final int TIME_FOR_WAIT_DO_IN_BACKGROUND_3S = 3;
    public static final int TIME_FOR_WAIT_DO_IN_BACKGROUND_7S = 7;
    public static final int TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL = 11;
    public static final int TIME_FOR_WAIT_DO_IN_BACKGROUND_20S = 20;

    public static String getStringFromInternet(String strUrl){
        StringBuilder builder = new StringBuilder();
        try{
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
            httpURLConnection.setReadTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (line != null){
                builder.append(line);
                line = reader.readLine();
            }

            reader.close();
            isr.close();
            is.close();
            httpURLConnection.disconnect();

        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return builder.toString();
    }

    public static String getStringFromInternet(String strUrl, int secondsTimeOut){
        StringBuilder builder = new StringBuilder();
        try{
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(secondsTimeOut * 1000);
            httpURLConnection.setReadTimeout(secondsTimeOut * 1000);
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (line != null){
                builder.append(line);
                line = reader.readLine();
            }

            reader.close();
            isr.close();
            is.close();
            httpURLConnection.disconnect();

        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return builder.toString();
    }

    public static JSONObject getJsonObjectFromInternet(String strUrl) throws JSONException{
        String strObj = getStringFromInternet(strUrl);
        JSONObject jsonObject;
        jsonObject = new JSONObject(strObj);

        return jsonObject;
    }

    public static JSONObject getJsonObjectFromInternet(String strUrl, int secondsTimeOut) throws JSONException{
        String strObj = getStringFromInternet(strUrl, secondsTimeOut);
        JSONObject jsonObject;
        jsonObject = new JSONObject(strObj);

        return jsonObject;
    }

    public static void getStringFromInternet_doInBackground_AsyncTask(final String strUrl, final OnMyFinishListener<String> finish){
        TaskBackground<String> task = new TaskBackground<String>(finish) {
            @Override
            protected String doInBackground_Something(String... params) {
                return getStringFromInternet(params[0]);
            }
        };task.execute(strUrl);
    }

    public static void getStringFromInternet_doInBackground_AsyncTask(final String strUrl, final int secondsTimeOut, final OnMyFinishListener<String> finish){
        TaskBackground<String> task = new TaskBackground<String>(finish) {
            @Override
            protected String doInBackground_Something(String... params) {
                return getStringFromInternet(params[0], secondsTimeOut);
            }
        };task.execute(strUrl);
    }

    public static void getJsonObjectFromInternet_doInBackground_AsyncTask(final String strUrl, final OnMyFinishListener<JSONObject> finish){
        TaskBackground<JSONObject> task = new TaskBackground<JSONObject>(finish) {
            @Override
            protected JSONObject doInBackground_Something(String... params) {
                try {
                    return getJsonObjectFromInternet(params[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };task.execute(strUrl);
    }

    public static void getJsonObjectFromInternet_doInBackground_AsyncTask(final String strUrl, final int secondsTimeOut, final OnMyFinishListener<JSONObject> finish){
        TaskBackground<JSONObject> task = new TaskBackground<JSONObject>(finish) {
            @Override
            protected JSONObject doInBackground_Something(String... params) {
                try {
                    return getJsonObjectFromInternet(params[0], secondsTimeOut);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };task.execute(strUrl);
    }

    public static AlertDialog createProcessDialog(Context context){
        AlertDialog.Builder dl = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_dialog_process, null);
        dl.setView(view);
        dl.setTitle(R.string.chu_dang_tai_du_lieu);
        dl.setCancelable(false);

        return dl.create();
    }

    public static void convertWpPostContentFitDevice(String strPostContent, OnMyFinishListener<String> onMyFinishListener){
        TaskBackground<String> task = new TaskBackground<String>(onMyFinishListener) {
            @Override
            protected String doInBackground_Something(String... params) {
                StringBuilder builder = new StringBuilder();
                String result1="", result2="", result3="", postContent = params[0], temp="";
                int id = postContent.indexOf("<figure class=\"wp-block-image\">");
                if (id != -1){
                    result1 = postContent.substring(0, id);
                    temp = postContent.substring(id);
                    id = temp.indexOf("src=");
                    if (id != -1){
                        result2 = temp.substring(0, id);
                        result3 = temp.substring(id);
                        builder.append(result1);
                        builder.append(result2+" style=\"width: 100%;\"  ");
                        builder.append(doInBackground(result3));
                    }
                    else
                        builder.append(postContent);
                }
                else
                    builder.append(postContent);

                return builder.toString();
            }
        };task.execute(strPostContent);
    }

    public static void getValueOfTagHTML(@NonNull String strContent,@NonNull String tag, OnMyFinishListener<String> onMyFinishListener){
        String tagEnd = "</"+tag+">";
        TaskBackground<String> task = new TaskBackground<String>(onMyFinishListener) {
            @Override
            protected String doInBackground_Something(String... params) {
                StringBuilder builder = new StringBuilder();
                String tmp = "", strContent = params[0], tag = params[1], tagEnd = params[2];
                int id = strContent.indexOf("<"+tag.trim());
                if (id != -1){
                    tmp = strContent.substring(id);
                    id = tmp.indexOf(">");
                    int id2 = tmp.indexOf(tagEnd);
                    if (id2 != -1){
                        builder.append(tmp.substring(id+1, id2));
                        builder.append(doInBackground(tmp.substring(id2), tag, tagEnd));
                    }
                }

                return builder.toString();
            }
        };task.execute(strContent, tag, tagEnd);
    }

    public static void getValueOfTagHTML_params(final String strContent, OnMyFinishListener<String> onMyFinishListener, String... tag){
        TaskBackground<String> task = new TaskBackground<String>(onMyFinishListener) {
            @Override
            protected String doInBackground_Something(String... params) {
                StringBuilder builder = new StringBuilder();
                for(int i = 0;i < params.length; i++){
                    builder.append(getValueOfTagHTML(strContent, params[i]));
                }

                return builder.toString();
            }
        };task.execute(tag);
    }

    private static String getValueOfTagHTML(String strContent, String tag){
        StringBuilder builder = new StringBuilder();
        String tagEnd = "</"+tag+">";
        int id = strContent.indexOf("<"+tag);
        if (id != -1){
            String tmp = strContent.substring(id);
            id = tmp.indexOf(">");
            if (id != -1) {
                tmp = tmp.substring(id);
                int id2 = tmp.indexOf(tagEnd);
                if (id2 != -1){
                    builder.append(tmp.substring(1, id2));
                    builder.append(getValueOfTagHTML(tmp.substring(id2), tag));
                }
            }
        }

        return builder.toString();
    }

    public static void showDialog(AlertDialog alertDialog){
        if (alertDialog == null) return;
        if (alertDialog.isShowing()) return;
        alertDialog.show();
    }

    public static void cancelDialog(AlertDialog alertDialog){
        if (alertDialog == null) return;
        if (alertDialog.isShowing()) alertDialog.cancel();
    }

    public static void addCommentsToTheBottomHTML(final Context context , final ArrayList<Wp_comment> wp_comments, final String postContent, OnMyFinishListener<String> onMyFinishListener){
        class Tmp extends TaskBackground<String>{

            public Tmp(OnMyFinishListener<String> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected String doInBackground_Something(String... params) {
                StringBuilder builder = new StringBuilder();
                builder.append(postContent);

                builder.append("<div style=\"width: 100%;height: auto;\">");//start
                builder.append("<hr style=\"margin-top: 60px; margin-bottom: 15px;\" />");
                builder.append("<link rel=\"stylesheet\" href=\""+context.getString(R.string.url_css_comment_wp_post)+"\">");

                for(Wp_comment wp_comment : wp_comments){
                    builder.append("<p class=\"khungBLChild\"><img src=\""+context.getString(R.string.url_image_user_male_50)+"\" class=\"anhNguoiBinhLuan\"><span class=\"tenBinhLuan\">");
                    builder.append("@"+wp_comment.getComment_author() +" : ");
                    builder.append(" </span>");
                    builder.append(wp_comment.getComment_content());
                    builder.append("<p>");
                }

                builder.append("</div>");//end

                return builder.toString();
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public static String hashMD5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentDayTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public static void sentMail(final String subject, final String body, final String mailTo, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                GMailSender sender = new GMailSender("sachcuhufi@gmail.com", "@myhufier");
                try {
                    sender.sendMail(subject, body, "My-Hufier", mailTo);
                    return true;
                } catch (Exception e) {
                    this.setBonusOfCoder("Fail To Sent Mail!");
                    e.printStackTrace();
                }

                return false;
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
        tmp.execute();
    }

    public static void getImage(String strContent, OnMyFinishListener<String> onMyFinishListener){
        TaskBackground<String> task = new TaskBackground<String>(onMyFinishListener) {
            @Override
            protected String doInBackground_Something(String... params) {
                String tmp = params[0];
                int id = tmp.indexOf("<figure class=\"wp-block-image\">");
                if (id != -1){
                    id = tmp.indexOf("<img src=");
                    if (id != -1){
                        tmp = tmp.substring(id);
                        id = tmp.indexOf("\"");
                        if (id != -1){
                            tmp = tmp.substring(id+1);
                            id = tmp.indexOf("\"");
                            if (id != -1)
                                return tmp.substring(0, id);
                        }
                    }
                }

                return null;
            }
        };task.execute(strContent);
    }

    public static void getTermID_from_wp_post_id(final String strUrl, OnMyFinishListener<Integer> onMyFinishListener){
        TaskBackground<Integer> task = new TaskBackground<Integer>(onMyFinishListener) {
            @Override
            protected Integer doInBackground_Something(String... params) {
                try {
                    JSONObject object = getJsonObjectFromInternet(strUrl, TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL);
                    JSONArray array = object.getJSONArray("term_id");
                    JSONObject jsonObject = array.getJSONObject(0);
                    return jsonObject.getInt("term_taxonomy_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return -1;
            }
        };task.execute();
    }

    public static void chuyenThanhChuThuong(String strSource, OnMyFinishListener<String> onMyFinishListener){
         class VNCharacterUtils {

            private final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
                    'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
                    'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
                    'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
                    'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
                    'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
                    'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
                    'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
                    'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
                    'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
                    'ữ', 'Ự', 'ự',};

            private final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
                    'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
                    'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
                    'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
                    'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
                    'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
                    'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
                    'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
                    'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
                    'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
                    'U', 'u', 'U', 'u',};

            private char removeAccent(char ch) {
                int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
                if (index >= 0) {
                    ch = DESTINATION_CHARACTERS[index];
                }
                return ch;
            }

            public String removeAccent(String str) {
                StringBuilder sb = new StringBuilder(str);
                for (int i = 0; i < sb.length(); i++) {
                    sb.setCharAt(i, removeAccent(sb.charAt(i)));
                }
                return sb.toString();
            }
        }

        class Tmp extends TaskBackground<String>{

            public Tmp(OnMyFinishListener<String> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected String doInBackground_Something(String... params) {
                VNCharacterUtils vnCharacterUtils = new VNCharacterUtils();
                return vnCharacterUtils.removeAccent(params[0]);
            }
        }

        Tmp tmp = new Tmp(onMyFinishListener);
         tmp.execute(strSource);
    }

    public static String VNCharacterToEnglishCharacter(String source){
        class VNCharacterUtils {

            private final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
                    'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
                    'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
                    'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
                    'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
                    'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
                    'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
                    'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
                    'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
                    'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
                    'ữ', 'Ự', 'ự',};

            private final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
                    'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
                    'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
                    'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
                    'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
                    'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
                    'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
                    'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
                    'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
                    'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
                    'U', 'u', 'U', 'u',};

            private char removeAccent(char ch) {
                int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
                if (index >= 0) {
                    ch = DESTINATION_CHARACTERS[index];
                }
                return ch;
            }

            public String removeAccent(String str) {
                StringBuilder sb = new StringBuilder(str);
                for (int i = 0; i < sb.length(); i++) {
                    sb.setCharAt(i, removeAccent(sb.charAt(i)));
                }
                return sb.toString();
            }
        }

        VNCharacterUtils vnCharacterUtils = new VNCharacterUtils();
        return vnCharacterUtils.removeAccent(source);
    }

    public static String getCodeVerify(){
        StringBuilder builder = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i< 6; i++)
            builder.append(rd.nextInt(10));
        return builder.toString();
    }

    public static void checkedUrlImage(final Context context , final String url, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                if (url.isEmpty())
                    return false;
                int id = url.lastIndexOf('.');
                if (id == -1)
                    return false;
                String exten = url.substring(url.lastIndexOf('.'));
                if (!exten.toLowerCase().equals(".jpg") && !exten.toLowerCase().equals(".png") && !exten.toLowerCase().equals(".jpeg")){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, ".jpg | .png | .jpeg", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }

                try {
                    URL checkUrl = new URL(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)checkUrl.openConnection();
                    httpURLConnection.setConnectTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    httpURLConnection.setReadTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    final Bitmap bitmap = BitmapFactory.decodeStream(checkUrl.openStream());
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, String.format("Image OK(%d , %d)", bitmap.getWidth(), bitmap.getHeight()), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                } catch (MalformedURLException e) {
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

    public static void checkLinkDownload(final String url, OnMyFinishListener<Boolean> onMyFinishListener){
        class Tmp extends TaskBackground<Boolean>{

            public Tmp(OnMyFinishListener<Boolean> onMyFinishListener) {
                super(onMyFinishListener);
            }

            @Override
            protected Boolean doInBackground_Something(String... params) {
                if (url.isEmpty())
                    return false;
                try {
                    URL checkUrl = new URL(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)checkUrl.openConnection();
                    httpURLConnection.setConnectTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);
                    httpURLConnection.setReadTimeout(TIME_FOR_WAIT_DO_IN_BACKGROUND_NORMAL * 1000);

                    return true;
                } catch (MalformedURLException e) {
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

    public static FunctionsStatic newInstance(){
        FunctionsStatic fun = new FunctionsStatic();
        return fun;
    }

    public Wp_user getUserWasLogin(Context context){
        Wp_user userWasLogin;
        SharedPreferences sp = context.getSharedPreferences("myHufierLogin", Context.MODE_PRIVATE);
        String uName = sp.getString("uName", null),
                fName = sp.getString("uFName", null),
                pass = sp.getString("uPass", null),
                email = sp.getString("uEmail", null),
                uid = sp.getString("uID", null),
                uUrl = sp.getString("uUrl", null);

        if (uName != null && fName != null && pass != null){
            userWasLogin = new Wp_user();
            userWasLogin.setUser_login(uName);
            userWasLogin.setDisplay_name(fName);
            userWasLogin.setUser_pass(pass);
            userWasLogin.setUser_email(email);
            try{
                userWasLogin.setID(Integer.parseInt(uid));
            }catch (NumberFormatException n){
                userWasLogin.setID(-1);
            }
            userWasLogin.setUser_url(uUrl);

            return userWasLogin;
        }
        else
            return null;
    }

    public static void hienThiThongBaoDialog(Context context, String title, String mess){
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(context);
        alBuilder.setTitle(title);
        alBuilder.setMessage(mess);
        alBuilder.setPositiveButton(context.getString(R.string.chu_dong_y), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alBuilder.create().show();
    }

    public static void hienThiThongBaoDialog(Context context, String title, String mess, DialogInterface.OnClickListener positiveButton){
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(context);
        alBuilder.setTitle(title);
        alBuilder.setMessage(mess);
        alBuilder.setPositiveButton(context.getString(R.string.chu_dong_y), positiveButton);
        alBuilder.create().show();
    }
}