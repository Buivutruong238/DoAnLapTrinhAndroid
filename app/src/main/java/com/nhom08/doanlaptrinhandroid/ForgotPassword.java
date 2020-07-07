package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import java.util.ArrayList;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();
    }

    private void init(){
        edtSentCode = findViewById(R.id.edtGetCodeForgotPassword);
        edtVerify = findViewById(R.id.edtCodeFromEmailForgotPassword);
        btnSenCode = findViewById(R.id.btnGetCodeForgotPassword);
        btnVerify = findViewById(R.id.btnVerifyCodeForgotPassword);
        divider = findViewById(R.id.dividerForgotPassword);
        tvImportant = findViewById(R.id.tvImportantForgotPassword);
        tvStep = findViewById(R.id.tvStepForgotPassword);
        btnSenCode.setOnClickListener(btnSenCodeClicked);
        btnVerify.setOnClickListener(btnVerifyClicked);
        processDialog = FunctionsStatic.createProcessDialog(this);
        wp_user_bll = new Wp_user_BLL();

        tvStep.setText(getString(R.string.chu_buoc)+" 1 / 2");

        //hidden view;
        divider.setVisibility(View.GONE);
        edtVerify.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);
        tvImportant.setVisibility(View.GONE);
        setLogoLogin_Hello();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.chu_doi_mat_khau).toUpperCase());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void setLogoLogin_Hello(){
        final ImageView imgLogoLogin = findViewById(R.id.imageViewForgotPassword);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgLogoLogin.animate().scaleX(0.1f).scaleY(0.1f).alpha(0).setDuration(2000).setStartDelay(2000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imgLogoLogin.animate().scaleX(1f).scaleY(1f).alpha(1).rotation(360 * 10).setDuration(2000).setStartDelay(500);
                }
            });
        }
    }

    private View.OnClickListener btnSenCodeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String emailFromUser = edtSentCode.getText().toString();
            if (emailFromUser.isEmpty()){
                edtSentCode.setError(getString(R.string.chu_cho_nay_khac_rong));
                edtSentCode.requestFocus();
                return;
            }

            if (!emailFromUser.contains("@")){
                edtSentCode.setError(getString(R.string.chu_email_can_phai_chua_a_cong));
                edtSentCode.requestFocus();
                return;
            }

            //check email exists
            FunctionsStatic.showDialog(processDialog);
            String strUrl = String.format(getString(R.string.url_wp_user), emailFromUser, emailFromUser);
            wp_user_bll.toArrayWp_users(strUrl, new OnMyFinishListener<ArrayList<Wp_user>>() {
                @Override
                public void onFinish(ArrayList<Wp_user> result) {
                    if (result.size() == 0){//email not exists
                        edtSentCode.setError(getString(R.string.chu_khong_tim_thay_email_nay));
                        FunctionsStatic.cancelDialog(processDialog);
                    }else{//email exists
                        //Sent code to user's email.
                        //first random code have 6 number.
                        codeVerify_client = FunctionsStatic.getCodeVerify();
                        FunctionsStatic.sentMail("Verify Your Account - @myhufier", "This is your code to VERIFY: "+codeVerify_client, emailFromUser, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean result) {
                                if (result){//sent ok
                                    setViewToVerify();
                                }else{//sent fail.
                                    Toast.makeText(ForgotPassword.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                                }
                                FunctionsStatic.cancelDialog(processDialog);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.cancelDialog(processDialog);
                                Toast.makeText(ForgotPassword.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.cancelDialog(processDialog);
                    Toast.makeText(ForgotPassword.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void setViewToVerify(){
        edtSentCode.setEnabled(false);
        btnSenCode.setClickable(false);
        divider.setVisibility(View.VISIBLE);
        divider.setTranslationY(-10000);
        edtVerify.setVisibility(View.VISIBLE);
        edtVerify.setTranslationX(-10000);
        btnVerify.setVisibility(View.VISIBLE);
        btnVerify.setTranslationX(10000);
        tvImportant.setVisibility(View.VISIBLE);
        tvImportant.setAlpha(0);

        btnSenCode.animate().alpha(0).setDuration(2000);
        divider.animate().translationY(0).setDuration(500);
        edtVerify.animate().translationX(0).setDuration(500).setStartDelay(500);
        btnVerify.animate().translationX(0).setDuration(500).setStartDelay(500);
        tvImportant.animate().alpha(1).rotation(360 * 10).setDuration(2000).setStartDelay(1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvStep.animate().rotation(360 * 10).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    tvStep.setText(getString(R.string.chu_buoc)+" 2 / 2");
                }
            });
        }else{
            tvStep.setText(getString(R.string.chu_buoc)+" 2 / 2");
        }
    }

    private View.OnClickListener btnVerifyClicked =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String codeFormEmail = edtVerify.getText().toString();
            if (codeFormEmail.equals(codeVerify_client)){
                Intent intent = new Intent(ForgotPassword.this, ChangePassword.class);
                intent.putExtra("user_email", edtSentCode.getText().toString());
                startActivity(intent);
                ForgotPassword.this.finish();
            }else{
                edtVerify.setError(getString(R.string.chu_ma_khong_khop));
                edtVerify.requestFocus();
            }
        }
    };

    private EditText edtSentCode, edtVerify;
    private Button btnSenCode, btnVerify;
    private View divider;
    private TextView tvImportant, tvStep;
    private AlertDialog processDialog;
    private Wp_user_BLL wp_user_bll;
    private String codeVerify_client;
}