package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_comment_BLL;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_post_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import java.util.ArrayList;

public class ViewDetailPostActivity extends AppCompatActivity {

    private int wp_post_id;
    private String wp_post_content, wp_post_content_changed;
    private String wp_post_guid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_post);

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(){
        wp_post_id = 0;
        wp_comment_bll = new Wp_comment_BLL();
        processDialog = FunctionsStatic.createProcessDialog(ViewDetailPostActivity.this);
        btnQuickComment = findViewById(R.id.btnBinhLuanNhanhViewPostDetail);
        btnQuickComment.setOnClickListener(btnQuickCommentClicked);
        edtQuickComment = findViewById(R.id.edtBinhLuanNhanhViewPostDetail);
        webView = findViewById(R.id.webViewWp_post);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(webViewClient);
        webView.setDownloadListener(webViewDownload);
        isShowQuickComment = false;

        //gone soon.
        edtQuickComment.setVisibility(View.GONE);
        btnQuickComment.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            wp_post_id = bundle.getInt("wp_post_id", -1);
            wp_post_content = bundle.getString("wp_post_content", null);
            wp_post_guid = bundle.getString("guid", "vietsaclo.ga");
        }

        if (wp_post_content != null)
            initWebView(wp_post_content);
        else
            FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_ket_noi_het_thoi_gian_kiem_tra_lai_ket_noi_internet));
    }

    private void initWebView(@NonNull String dataContent){
        final AlertDialog processDialog = FunctionsStatic.createProcessDialog(ViewDetailPostActivity.this);
        FunctionsStatic.showDialog(processDialog);
        FunctionsStatic.convertWpPostContentFitDevice(dataContent, new OnMyFinishListener<String>() {
            @Override
            public void onFinish(final String dataContent_fixImage) {
                @SuppressLint("DefaultLocale") String urlComment = String.format("%s?comment_post_ID=%d",getString(R.string.url_wp_comments), wp_post_id);
                new Wp_comment_BLL().toArrayWp_comment(urlComment, new OnMyFinishListener<ArrayList<Wp_comment>>() {
                    @Override
                    public void onFinish(ArrayList<Wp_comment> comments) {
                        FunctionsStatic.addCommentsToTheBottomHTML(ViewDetailPostActivity.this, comments, dataContent_fixImage, new OnMyFinishListener<String>() {
                            @Override
                            public void onFinish(String dataContent_fixImage_comments) {
                                wp_post_content_changed = "<div style=\"width: 100%; height: auto;\">" +"<div style=\"padding: 10px;\">"
                                        +dataContent_fixImage_comments+"</div></div>";
                                try{
                                    isShowQuickComment = true;
                                    webView.loadData(wp_post_content_changed, "text/html;charset=utf-8", "UTF-8");
                                }catch (Exception e){
                                    FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_co_ve_nhu_thiet_bi_cua_ban_khong_ho_tro_tinh_nang_nay_vui_long_bat_che_do_xem_nhu_web));
                                }
                                FunctionsStatic.cancelDialog(processDialog);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.cancelDialog(processDialog);
                                FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_co_ve_nhu_thiet_bi_cua_ban_khong_ho_tro_tinh_nang_nay_vui_long_bat_che_do_xem_nhu_web));
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error, Object bonusOfCoder) {
                        FunctionsStatic.cancelDialog(processDialog);
                        FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_co_ve_nhu_thiet_bi_cua_ban_khong_ho_tro_tinh_nang_nay_vui_long_bat_che_do_xem_nhu_web));
                    }
                });
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.cancelDialog(processDialog);
                FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_co_ve_nhu_thiet_bi_cua_ban_khong_ho_tro_tinh_nang_nay_vui_long_bat_che_do_xem_nhu_web));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_menu_view_post_detail);
        aSwitchViewSuchWeb = findViewById(R.id.app_bar_switch_view_post_detail);
        aSwitchViewSuchWeb.setOnCheckedChangeListener(aSwitchViewSuchWebCheckedChange);
        progressBarLoadingWebView = findViewById(R.id.processBarLoadingWebViewDetailPost);
        btnGoNext = findViewById(R.id.btnGoNext_viewPostDetail);
        btnGoBack = findViewById(R.id.btnGoBack_viewPostDetail);
        btnGoNext.setOnClickListener(btnGoNextClicked);
        btnGoBack.setOnClickListener(btnGoBackClicked);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener btnQuickCommentClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Wp_user user = getUserWasLogin();
            if (user == null){
                showAlertUserNeedLogin();
            }else{
                String tComment = edtQuickComment.getText().toString();
                if (tComment.isEmpty()) {
                    edtQuickComment.setText(getString(R.string.thankAuthor));
                    return;
                }
                //comment.
                Wp_comment comment = new Wp_comment();
                comment.setComment_post_ID(wp_post_id);
                comment.setComment_author(user.getDisplay_name());
                comment.setComment_author_email(user.getUser_email());
                comment.setComment_author_url(user.getUser_url());
                comment.setComment_date(FunctionsStatic.getCurrentDayTime());
                comment.setComment_content(tComment);
                comment.setUser_id(user.getID());

                FunctionsStatic.showDialog(processDialog);
                wp_comment_bll.addComment(getString(R.string.url_insert_comment), comment, new OnMyFinishListener<Boolean>() {
                    @Override
                    public void onFinish(Boolean result) {
                        if (result) {
                            initWebView(wp_post_content);
                            edtQuickComment.setText(null);
                        }
                        else
                            Toast.makeText(ViewDetailPostActivity.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                        FunctionsStatic.cancelDialog(processDialog);
                    }

                    @Override
                    public void onError(Throwable error, Object bonusOfCoder) {
                        FunctionsStatic.cancelDialog(processDialog);
                        Toast.makeText(ViewDetailPostActivity.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private Wp_user getUserWasLogin(){
        Wp_user user = null;
        SharedPreferences sp = getSharedPreferences("myHufierLogin", MODE_PRIVATE);
        if (sp != null){
            String uName = sp.getString("uName", null),
                    fName = sp.getString("uFName", null),
                    pass = sp.getString("uPass", null),
                    email = sp.getString("uEmail", null),
                    uid = sp.getString("uID", null),
                    uUrl = sp.getString("uUrl", null);
            if (uName != null){
                user = new Wp_user();
                user.setUser_login(uName);
                user.setDisplay_name(fName);
                user.setUser_pass(pass);
                user.setUser_email(email);
                user.setID(Integer.parseInt(uid));
                user.setUser_url(uUrl);
            }
        }

        return user;
    }

    private void showAlertUserNeedLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.chu_thong_bao);
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setMessage(R.string.chu_can_dang_nhap_de_tiep_tuc);
        builder.setNegativeButton(R.string.chu_thoat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.chu_dang_nhap, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(ViewDetailPostActivity.this, Login.class);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private CompoundButton.OnCheckedChangeListener aSwitchViewSuchWebCheckedChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Toast.makeText(ViewDetailPostActivity.this, R.string.chu_dang_tai_du_lieu, Toast.LENGTH_LONG).show();
                webView.loadUrl(wp_post_guid);
            }
            else
                initWebView(wp_post_content);
        }
    };

    private WebViewClient webViewClient = new WebViewClient(){
        private int percent = 0;
        private int MAX = 110;
        private final int MAX_LOAD = 90;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (isShowQuickComment)
                showQuickComment();
            else
                hideQuickComment();

            //FunctionsStatic.showDialog(processDialog);
            percent = 0;
            progressBarLoadingWebView.setMax(MAX);
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (percent <= MAX_LOAD){
                        progressBarLoadingWebView.incrementProgressBy(1);
                        percent += 1;
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.post(runnable);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            isShowQuickComment = false;

            //FunctionsStatic.cancelDialog(processDialog);
            progressBarLoadingWebView.setProgress(MAX);
            percent = MAX;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarLoadingWebView.setProgress(0);
                }
            }, 1000);
        }
    };

    private void showQuickComment(){
        edtQuickComment.animate().translationY(0).setDuration(2000);
        btnQuickComment.animate().translationY(0).setDuration(2000);
        edtQuickComment.setVisibility(View.VISIBLE);
        btnQuickComment.setVisibility(View.VISIBLE);
    }

    private void hideQuickComment(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnQuickComment.animate().translationY(500).setDuration(2000);
            edtQuickComment.animate().translationY(500).setDuration(2000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    edtQuickComment.setVisibility(View.GONE);
                    btnQuickComment.setVisibility(View.GONE);
                }
            });
        }else{
            edtQuickComment.setVisibility(View.GONE);
            btnQuickComment.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener btnGoBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (webView.canGoBack())
                webView.goBack();
        }
    };

    private View.OnClickListener btnGoNextClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (webView.canGoForward())
                webView.goForward();
        }
    };

    private DownloadListener webViewDownload = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));
            request.setMimeType(mimetype);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading File...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                            url, contentDisposition, mimetype));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            FunctionsStatic.hienThiThongBaoDialog(ViewDetailPostActivity.this, getString(R.string.chu_thong_bao), getString(R.string.chu_tep_cua_ban_dang_tai_ve));
        }
    };

    private Wp_comment_BLL wp_comment_bll;
    private AlertDialog processDialog;
    private Button btnQuickComment;
    private EditText edtQuickComment;
    private WebView webView;
    private Switch aSwitchViewSuchWeb;
    private ProgressBar progressBarLoadingWebView;
    private Button btnGoBack, btnGoNext;
    private boolean isShowQuickComment;
}