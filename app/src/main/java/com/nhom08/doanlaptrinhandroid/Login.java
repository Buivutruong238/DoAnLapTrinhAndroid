package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init(){
        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        wp_user_bll = new Wp_user_BLL();
        processDialog = FunctionsStatic.createProcessDialog(this);
        tvForgotPassword = findViewById(R.id.tvForgotPasswordLogin);

        edtUserName.addTextChangedListener(edtUserNameTextChanged);
        edtPassword.addTextChangedListener(edtPasswordTextChanged);
        btnLogin.setOnClickListener(btnLoginClicked);
        tvForgotPassword.setOnClickListener(tvForgotPasswordClicked);

        ImageView imageViewLogo = findViewById(R.id.imgLogoLogin);
        imageViewLogo.setOnClickListener(imageViewLogoClicked);

        setLogoLogin_Hello();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.chu_dang_nhap).toUpperCase());


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private TextWatcher edtUserNameTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String uName = s.toString(), pass = edtPassword.getText().toString();
            btnLogin.setEnabled(!uName.isEmpty() && !pass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher edtPasswordTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String pass = s.toString(), uName = edtUserName.getText().toString();
            btnLogin.setEnabled(!uName.isEmpty() && !pass.isEmpty());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener btnLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FunctionsStatic.showDialog(processDialog);
            String str = getString(R.string.url_wp_user);
            final String userLogin = edtUserName.getText().toString();
            str = String.format(str, userLogin, userLogin);
            wp_user_bll.toArrayWp_users(str, new OnMyFinishListener<ArrayList<Wp_user>>() {
                @Override
                public void onFinish(ArrayList<Wp_user> result) {
                    if(result.size() == 0){
                        if (userLogin.contains("@"))
                            edtUserName.setError(getString(R.string.chu_khong_tim_thay_email_nay));
                        else
                            edtUserName.setError(getString(R.string.chu_khong_tim_thay_username_nay));
                        edtUserName.requestFocus();
                    }
                    else{
                        String passGet = result.get(0).getUser_pass(), pass = edtPassword.getText().toString();
                        pass = FunctionsStatic.hashMD5(pass);
                        if (pass.equals(passGet)) {
                            saveUserLoginSuccess(result.get(0));
                        }
                        else {
                            edtPassword.setError(getString(R.string.chu_mat_khau_khong_khop));
                            edtPassword.requestFocus();
                        }
                    }

                    FunctionsStatic.cancelDialog(processDialog);
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.cancelDialog(processDialog);
                    Toast.makeText(Login.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void saveUserLoginSuccess(Wp_user user){
        SharedPreferences sp = getSharedPreferences("myHufierLogin",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uID", String.valueOf(user.getID()));
        editor.putString("uEmail", user.getUser_email());
        editor.putString("uName", user.getUser_login());
        editor.putString("uFName", user.getDisplay_name());
        editor.putString("uPass", user.getUser_pass());
        editor.putString("uUrl", user.getUser_url());
        editor.apply();

        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private View.OnClickListener tvForgotPasswordClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
            Login.this.finish();
        }
    };

    private void setLogoLogin_Hello(){
        final ImageView imgLogoLogin = findViewById(R.id.imgLogoLogin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgLogoLogin.animate().scaleX(0.1f).scaleY(0.1f).alpha(0).setDuration(2000).setStartDelay(2000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    imgLogoLogin.animate().scaleX(1f).scaleY(1f).alpha(1).rotation(360 * 10).setDuration(2000).setStartDelay(500);
                }
            });
        }
    }

    private View.OnClickListener imageViewLogoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setLogoLogin_Hello();
        }
    };

    private EditText edtUserName, edtPassword;
    private Button btnLogin;
    private Wp_user_BLL wp_user_bll;
    private AlertDialog processDialog;
    private TextView tvForgotPassword;
}