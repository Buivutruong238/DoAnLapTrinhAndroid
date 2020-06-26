package com.nhom08.doanlaptrinhandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_post_BLL;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_term_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_term;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import java.util.ArrayList;

public class DangBaiMoi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_bai_moi);

        init();
    }

    private void init(){
        tvTheLoai = findViewById(R.id.tvTheLoaiDangBaiMoi);
        Button btnChonTheLoai = findViewById(R.id.btnChonTheLoaiDangBaiMoi);
        Button btnThoat = findViewById(R.id.btnThoatDangBaiMoi);
        Button btnDangBai = findViewById(R.id.btnDangBaiNgayDangBaiMoi);
        Button btnNangCao = findViewById(R.id.btnNangCaoDangBaiMoi);
        processDialog = FunctionsStatic.createProcessDialog(this);
        TextView tvConent = findViewById(R.id.edtNoiDungDangBaiMoi);
        tvConent.setMovementMethod(new ScrollingMovementMethod());

        btnChonTheLoai.setOnClickListener(btnChonTheLoaiClicked);
        btnThoat.setOnClickListener(btnThoatClicked);
        btnDangBai.setOnClickListener(btnDangBaiClicked);
        btnNangCao.setOnClickListener(btnNangCaoClicked);

        getUserWasLogin();
        getTerms();
    }

    private View.OnClickListener btnChonTheLoaiClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showListTermsDialog(terms);
        }
    };

    private void getTerms(){
        FunctionsStatic.showDialog(processDialog);
        new Wp_term_BLL().toArrayWp_terms(getString(R.string.url_wp_terms), new OnMyFinishListener<ArrayList<Wp_term>>() {
            @Override
            public void onFinish(ArrayList<Wp_term> result) {
                if (result.size() != 0){
                    terms = result;
                    termChoose = terms.get(0);
                    tvTheLoai.setText(termChoose.getName());
                }else{
                    DangBaiMoi.this.finish();
                }
                FunctionsStatic.cancelDialog(processDialog);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.cancelDialog(processDialog);
                DangBaiMoi.this.finish();
            }
        });
    }

    private void showListTermsDialog(final ArrayList<Wp_term> terms){
        AlertDialog.Builder builder = new AlertDialog.Builder(DangBaiMoi.this);
        builder.setTitle(getString(R.string.chu_chon_the_loai));
        String[] strings = new String[terms.size()];
        for(int i = 0; i< terms.size(); i++)
            strings[i] = terms.get(i).getName();

        builder.setSingleChoiceItems(strings, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                termChoose = terms.get(which);
                tvTheLoai.setText(termChoose.getName());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.chu_hanh_dong_khong_chap_nhan, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener btnThoatClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DangBaiMoi.this);
            builder.setTitle(R.string.chu_can_than);
            builder.setMessage(R.string.chu_hoi_co_muon_thoat_khong);
            builder.setPositiveButton(R.string.chu_dong_y, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    DangBaiMoi.this.finish();
                }
            });
            builder.setNegativeButton(R.string.chu_thoat, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
    };

    private View.OnClickListener btnDangBaiClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText edtTitle = findViewById(R.id.edtTitleDangBanMoi);
            //not null?
            final String title = edtTitle.getText().toString().trim();
            if (title.isEmpty()){
                edtTitle.setError(getString(R.string.chu_ten_bai_viet_khac_rong));
                edtTitle.requestFocus();
                return;
            }

            TextView edtContent = findViewById(R.id.edtNoiDungDangBaiMoi);
            final String content = edtContent.getText().toString();
            if (content.isEmpty()){
                edtContent.setError(getString(R.string.chu_noi_dung_bai_viet_khac_rong));
                edtContent.requestFocus();
                return;
            }

            //Title exists?
            final Wp_post_BLL wp_post_bll = new Wp_post_BLL();
            final String strGetPostByTitle = String.format(getString(R.string.url_wp_post_by_title), title).replace(" ", "%20");
            FunctionsStatic.showDialog(processDialog);
            wp_post_bll.toArrayWp_posts(strGetPostByTitle, new OnMyFinishListener<ArrayList<Wp_post>>() {
                @Override
                public void onFinish(ArrayList<Wp_post> result) {
                    if (result.size() == 0){//Not Exists;
                        //Them Vao Database.
                        Wp_post wp_post = new Wp_post();
                        wp_post.setPost_author(userWasLogin.getID());
                        String postDay = FunctionsStatic.getCurrentDayTime();
                        wp_post.setPost_date(postDay);
                        wp_post.setPost_modified(postDay);
                        wp_post.setPost_content(content);
                        wp_post.setPost_title(title);
                        wp_post.setPost_name(FunctionsStatic.VNCharacterToEnglishCharacter(title).replace(" ", "-"));
                        wp_post_bll.addWpPost(getString(R.string.url_insert_wp_post), wp_post, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(final Boolean isInserted) {
                                if (isInserted){//success
                                    //update guid.
                                    //getID by title.
                                    wp_post_bll.toArrayWp_posts(strGetPostByTitle, new OnMyFinishListener<ArrayList<Wp_post>>() {
                                        @Override
                                        public void onFinish(ArrayList<Wp_post> wp_post_get_by_title) {
                                            if (wp_post_get_by_title.size() != 0){//get ok
                                                final int ID = wp_post_get_by_title.get(0).getID();
                                                //update guid.
                                                wp_post_bll.updateGuid(getString(R.string.url_update_guid), ID, getString(R.string.url_host), new OnMyFinishListener<Boolean>() {
                                                    @Override
                                                    public void onFinish(Boolean isUpdated) {
                                                        if (isUpdated){//ok
                                                            //insert term relationship;
                                                            wp_post_bll.insertTermRelationship(getString(R.string.url_insert_term_relationship), ID, termChoose.getTerm_id(), new OnMyFinishListener<Boolean>() {
                                                                @Override
                                                                public void onFinish(Boolean isInsertTermRelationShip) {
                                                                    if (isInsertTermRelationShip){
                                                                        FunctionsStatic.cancelDialog(processDialog);
                                                                        hienThiThongBaoThanhCong();
                                                                    }else{
                                                                        FunctionsStatic.cancelDialog(processDialog);
                                                                        Toast.makeText(DangBaiMoi.this, "Fail For Insert 4", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(Throwable error, Object bonusOfCoder) {
                                                                    FunctionsStatic.cancelDialog(processDialog);
                                                                    Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"5", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }else{//fail
                                                            FunctionsStatic.cancelDialog(processDialog);
                                                            Toast.makeText(DangBaiMoi.this, "Fail For Insert 3", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable error, Object bonusOfCoder) {
                                                        FunctionsStatic.cancelDialog(processDialog);
                                                        Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"4", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }else{//get fail
                                                FunctionsStatic.cancelDialog(processDialog);
                                                Toast.makeText(DangBaiMoi.this, "Fail For Insert 2", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable error, Object bonusOfCoder) {
                                            FunctionsStatic.cancelDialog(processDialog);
                                            Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"3", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    FunctionsStatic.cancelDialog(processDialog);
                                    Toast.makeText(DangBaiMoi.this, "Fail For Insert 1", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {//insert fail
                                FunctionsStatic.cancelDialog(processDialog);
                                Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"2", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{//Exists.
                        FunctionsStatic.cancelDialog(processDialog);
                        edtTitle.setError(getString(R.string.chu_title_da_ton_tai));
                        edtTitle.requestFocus();
                    }
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.cancelDialog(processDialog);
                    Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"1", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void hienThiThongBaoThanhCong(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.chu_chuc_mung);
        builder.setMessage(R.string.chu_bai_viet_cua_ban_duoc_tai_len_thanh_cong);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.chu_dong_y, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                DangBaiMoi.this.finish();
            }
        });
        builder.create().show();
    }

    private void getUserWasLogin(){
        userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(this);
        if (userWasLogin == null)
            DangBaiMoi.this.finish();
    }

    final int[] paragraph = {0};//variable for btnNangCaoClicked
    private View.OnClickListener btnNangCaoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(DangBaiMoi.this);
            alBuilder.setTitle(R.string.chu_nang_cao);
            View layout = getLayoutInflater().inflate(R.layout.layout_nang_cao_dang_bai_moi, null);
            alBuilder.setView(layout);
            alBuilder.setCancelable(false);
            final AlertDialog dialog = alBuilder.create();
            dialog.show();

            Button btnHuy = layout.findViewById(R.id.btnHuyNangCaoDangBaiMoi);
            Button btnDongY = layout.findViewById(R.id.btnDongYNangCaoDangBaiMoi);
            final ProgressBar progressBar = layout.findViewById(R.id.progressBarNangCaoDangBaiMoi);
            progressBar.setVisibility(View.GONE);
            RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupNangCaoDangBaiMoi);
            final TextView edtNoiDung = findViewById(R.id.edtNoiDungDangBaiMoi);
            final EditText edtNhapURL = dialog.findViewById(R.id.edtNhapUrlDangBaiMoi);

            final int[] idRadioChecked = {radioGroup.getCheckedRadioButtonId()};
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    idRadioChecked[0] = checkedId;
                    switch (checkedId){
                        case R.id.radioButtonThemMotDoanVanNangCaoDangBaiMoi:{
                            edtNhapURL.setHint(R.string.chu_them_mot_doan_van);
                            break;
                        }
                        case R.id.radioButtonThemHinhDangBaiMoi:{
                            edtNhapURL.setHint(R.string.chu_them_mot_link_hinh_anh);
                            break;
                        }
                        case R.id.radioButtonThemLinkDownloadDangBaiMoi:{
                            edtNhapURL.setHint(R.string.chu_them_mot_link_download);
                            break;
                        }
                        default: break;
                    }
                }
            });

            btnHuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            btnDongY.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    final String strInsertInput = edtNhapURL.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    final String BEGIN = "<!-- BEGIN EDIT -->";
                    final String END = "\n\n<!-- END EDIT PARAGRAPH %d-->\n";
                    switch (idRadioChecked[0]){
                        case R.id.radioButtonThemMotDoanVanNangCaoDangBaiMoi:{
                            String strNoiDung = edtNoiDung.getText().toString();

                            if (strNoiDung.isEmpty())
                                edtNoiDung.setText(BEGIN+"\n");

                            @SuppressLint("DefaultLocale")
                            String stringBuilder = edtNoiDung.getText().toString() +
                                    "\n" +
                                    "<div style=\"margin-top: 50px; \"></div>\n" +
                                    "<p>" + strInsertInput + "</p>" +
                                    String.format(END, ++paragraph[0]);
                            edtNoiDung.setText(stringBuilder);
                            progressBar.setVisibility(View.GONE);
                            dialog.cancel();

                            break;
                        }
                        case R.id.radioButtonThemHinhDangBaiMoi:{
                            FunctionsStatic.checkedUrlImage(DangBaiMoi.this, strInsertInput, new OnMyFinishListener<Boolean>() {
                                @Override
                                public void onFinish(Boolean checkOk) {
                                    if (checkOk){//strInsertInput found
                                        String strNoiDung = edtNoiDung.getText().toString();

                                        if (strNoiDung.isEmpty())
                                            edtNoiDung.setText(BEGIN);

                                        @SuppressLint("DefaultLocale")
                                        String stringBuilder = edtNoiDung.getText().toString() +//old value;
                                                "\n" +//new line;
                                                "<!-- wp:image -->\n" +
                                                "<div style=\"margin-top: 50px; \"></div>\n" +
                                                "<figure class=\"wp-block-image\"><img src=\"" +
                                                strInsertInput +
                                                "\" alt=\"Image Upload from app\"/></figure>\n" +
                                                "<!-- /wp:image -->" +
                                                String.format(END, ++paragraph[0]);
                                        edtNoiDung.setText(stringBuilder);
                                        dialog.cancel();
                                    }else{//not found
                                        edtNhapURL.setError(getString(R.string.chu_url_khong_dung_dinh_dang_hoac_khong_tim_thay_tren_internet));
                                        edtNhapURL.requestFocus();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Throwable error, Object bonusOfCoder) {
                                    Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"CheckUrlImage1", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                            break;
                        }
                        case R.id.radioButtonThemLinkDownloadDangBaiMoi:{
                            //check link download ok.
                            FunctionsStatic.checkLinkDownload(strInsertInput, new OnMyFinishListener<Boolean>() {
                                @Override
                                public void onFinish(Boolean isLinkOk) {
                                    if (isLinkOk){
                                        String strNoiDung = edtNoiDung.getText().toString();

                                        if (strNoiDung.isEmpty())
                                            edtNoiDung.setText(BEGIN);

                                        @SuppressLint("DefaultLocale")
                                        String stringBuilder = edtNoiDung.getText().toString() +//old value;
                                                "\n" +//new line;
                                                "<!-- wp:html -->" +
                                                "<div style=\"margin-top: 50px; \"></div>\n" +
                                                "<h3 style=\"color: red\">Báo lỗi!</h3><div><i><p style=\"color: red;\">Mọi thắc mắc vui lòng để lại bình luận dưới bài viết <big style=\"color: black;\">nếu link hỏng hãy báo cho</big> <a href=\"" +
                                                userWasLogin.getUser_url() +
                                                "\" target=\"_blank\">" + userWasLogin.getDisplay_name() + "</a></p></i></div>" +
                                                "<link rel=\"stylesheet\" href=\"" + getString(R.string.url_css_button_download) + "\">" +
                                                "<div class=\"border-btn-download\"><a href=\"" +
                                                strInsertInput +
                                                "\" target=\"_blank\"><button id=\"myBtnDownload\">Viet_SacLo</button></a></div>" +
                                                "<!-- /wp:html -->" +
                                                String.format(END, ++paragraph[0]);
                                        edtNoiDung.setText(stringBuilder);
                                        dialog.cancel();
                                    }else{
                                        edtNhapURL.setError(getString(R.string.chu_url_khong_dung_dinh_dang_hoac_khong_tim_thay_tren_internet));
                                        edtNhapURL.requestFocus();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Throwable error, Object bonusOfCoder) {
                                    Toast.makeText(DangBaiMoi.this, bonusOfCoder.toString()+"CheckUrlDownload1", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                            break;
                        }
                        default: break;
                    }
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setTitle(R.string.chu_dang_bai_viet);

        return super.onCreateOptionsMenu(menu);
    }

    private TextView tvTheLoai;
    private ArrayList<Wp_term> terms;
    private Wp_term termChoose;
    private AlertDialog processDialog;
    private Wp_user userWasLogin;
}