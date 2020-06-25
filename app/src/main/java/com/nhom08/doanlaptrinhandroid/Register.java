package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import java.util.ArrayList;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init(){
        edtUserName = findViewById(R.id.edtUsernameRegister);
        edtEmail = findViewById(R.id.edtEmailRegister);
        edtFullName = findViewById(R.id.edtFullNameRegister);
        edtPass = findViewById(R.id.edtPassRegister);
        edtRetypePass = findViewById(R.id.edtRetypePassRegister);
        btnRegisterNow = findViewById(R.id.btnRegisterNow);
        edtCode = findViewById(R.id.edtCodeFromEmailForgotPassword);
        btnVerifyCode = findViewById(R.id.btnVerifyCodeRegister);
        dividerHz = findViewById(R.id.dividerForgotPassword);
        tvImportant = findViewById(R.id.tvQuanTrongRegister);
        progressBar = findViewById(R.id.processBarRegister);
        processDialog = FunctionsStatic.createProcessDialog(this);
        Switch aSwitch = findViewById(R.id.switchRegisterFast);

        wp_user_bll = new Wp_user_BLL();

        dividerHz.setTranslationY(-10000f);
        edtCode.setTranslationY(-10000f);
        btnVerifyCode.setTranslationY(-10000f);
        tvImportant.setTranslationY(-10000f);
        progressBar.setVisibility(View.GONE);

        btnRegisterNow.setOnClickListener(btnRegisterNowClicked);
        btnVerifyCode.setOnClickListener(btnVerifyClicked);
        aSwitch.setOnCheckedChangeListener(aSwitchCheckedChange);
    }

    private View.OnClickListener btnRegisterNowClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (progressBar.getVisibility() == View.VISIBLE)
                return;//Wait to process finish
            checkUserNameAndEmailExists(edtUserName.getText().toString(), edtEmail.getText().toString());
        }
    };

    private View.OnClickListener btnVerifyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String codeGet = edtCode.getText().toString();
            if (codeGet.equals(codeVerify)){
                addUserToDB(userVerified);
            }else{
                edtCode.setError(getString(R.string.chu_ma_khong_khop));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.chu_dang_ky).toUpperCase());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void loadAnimationViewVerify(){
        //Xac Minh
        progressBar.setVisibility(View.GONE);
        dividerHz.animate().translationY(0).setDuration(1000);
        edtCode.animate().translationY(0).setDuration(1300);
        btnVerifyCode.animate().translationY(0).setDuration(1500);
        tvImportant.animate().translationY(0).rotation(1440).setDuration(2100);
    }

    private void checkUserNameAndEmailExists(final String uName, final String email){
        //check empty
        if (uName.isEmpty()){
            edtUserName.setError(getString(R.string.chu_cho_nay_khac_rong));
            edtUserName.requestFocus();
            return;
        }
        if (email.isEmpty()){
            edtEmail.setError(getString(R.string.chu_cho_nay_khac_rong));
            edtEmail.requestFocus();
            return;
        }

        if (!email.contains("@")){
            edtEmail.setError(getString(R.string.chu_email_can_phai_chua_a_cong));
            edtEmail.requestFocus();
            return;
        }

        final String fName = edtFullName.getText().toString();
        if (fName.isEmpty()){
            edtFullName.setError(getString(R.string.chu_cho_nay_khac_rong));
            edtFullName.requestFocus();
            return;
        }

        final String pass = edtPass.getText().toString();
        if (pass.isEmpty()){
            edtPass.setError(getString(R.string.chu_cho_nay_khac_rong));
            edtPass.requestFocus();
            return;
        }

        String retypePass = edtRetypePass.getText().toString();
        if (retypePass.isEmpty()){
            edtRetypePass.setError(getString(R.string.chu_cho_nay_khac_rong));
            edtRetypePass.requestFocus();
            return;
        }

        if (!pass.equals(retypePass)){
            edtRetypePass.setError(getString(R.string.chu_mat_khau_khong_khop));
            edtRetypePass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.url_wp_user);
        //Check Username or Email Exists
        wp_user_bll.toArrayWp_users(String.format(url, uName, email), new OnMyFinishListener<ArrayList<Wp_user>>() {
            @Override
            public void onFinish(ArrayList<Wp_user> result) {
                if(result.size() == 0){
                    //Sent Email To
                    FunctionsStatic.showDialog(processDialog);
                    codeVerify = FunctionsStatic.getCodeVerify();
                    FunctionsStatic.sentMail("Verify Your Account - @myhufier", "This is your code to VERIFY: "+codeVerify, email, new OnMyFinishListener<Boolean>() {
                        @Override
                        public void onFinish(Boolean result) {
                            if (result){
                                loadAnimationViewVerify();
                                //Initial user to add database after Verify//Request: user_login, user_pass, user_email, user_registered, display_name
                                Wp_user user = new Wp_user();
                                user.setUser_login(uName);
                                user.setUser_email(email);
                                user.setDisplay_name(fName);
                                user.setUser_pass(pass);
                                user.setUser_registered(FunctionsStatic.getCurrentDayTime());
                                userVerified = user;
                            }else{
                                Toast.makeText(Register.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                            }
                            FunctionsStatic.cancelDialog(processDialog);
                            progressBar.setVisibility(View.GONE);
                            btnRegisterNow.setEnabled(false);
                        }

                        @Override
                        public void onError(Throwable error, Object bonusOfCoder) {
                            FunctionsStatic.cancelDialog(processDialog);
                            progressBar.setVisibility(View.GONE);
                            btnRegisterNow.setEnabled(false);
                            Toast.makeText(Register.this, "Error: "+bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Wp_user user = result.get(0);
                    if (user.getUser_login().equals(uName)){
                        edtUserName.setError(getString(R.string.chu_ten_nguoi_dung_da_ton_tai));
                        edtUserName.requestFocus();
                    }
                    else{
                        edtEmail.setError(getString(R.string.chu_email_da_ton_tai));
                        edtEmail.requestFocus();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Register.this, "Error: "+bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToDB(final Wp_user user){
        FunctionsStatic.showDialog(processDialog);
        wp_user_bll.addUser(getString(R.string.url_insert_user), user, new OnMyFinishListener<Boolean>() {
            @Override
            public void onFinish(Boolean result) {
                if (result) {
                    String url = String.format(getString(R.string.url_wp_user), user.getUser_login(), user.getUser_email());
                    wp_user_bll.toArrayWp_users(url, new OnMyFinishListener<ArrayList<Wp_user>>() {
                        @Override
                        public void onFinish(ArrayList<Wp_user> result) {
                            if (result.size() > 0){
                                Wp_user user = result.get(0);
                                //Save Preferences
                                SharedPreferences sp = getSharedPreferences("myHufierLogin", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("uID", String.valueOf(user.getID()));
                                editor.putString("uEmail", user.getUser_email());
                                editor.putString("uName", user.getUser_login());
                                editor.putString("uFName", user.getDisplay_name());
                                editor.putString("uPass", user.getUser_pass());
                                editor.putString("uUrl", user.getUser_url());
                                editor.apply();

                                Intent intent = new Intent(Register.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else
                                Toast.makeText(Register.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                            FunctionsStatic.cancelDialog(processDialog);
                        }

                        @Override
                        public void onError(Throwable error, Object bonusOfCoder) {
                            Toast.makeText(Register.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                            FunctionsStatic.cancelDialog(processDialog);
                        }
                    });
                }else {
                    Toast.makeText(Register.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                    FunctionsStatic.cancelDialog(processDialog);
                }
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.cancelDialog(processDialog);
                Toast.makeText(Register.this,  R.string.chu_loi_ket_noi_toi_internet+bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener aSwitchCheckedChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                Intent intent = new Intent(Register.this, FastRegister.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private EditText edtUserName, edtEmail, edtFullName, edtPass, edtRetypePass, edtCode;
    private Button btnRegisterNow, btnVerifyCode;
    private View dividerHz;
    private TextView tvImportant;
    private ProgressBar progressBar;
    private AlertDialog processDialog;

    private Wp_user_BLL wp_user_bll;
    private Wp_user userVerified;
    private String codeVerify;
}