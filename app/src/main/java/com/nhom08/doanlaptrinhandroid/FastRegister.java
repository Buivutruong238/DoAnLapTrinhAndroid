package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.FastRegisterStatus;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import java.util.ArrayList;

public class FastRegister extends AppCompatActivity {

    private static final int MAX_STEP = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_register);

        init();
    }

    @SuppressLint("DefaultLocale")
    private void init(){
        isLoading = false;
        status = FastRegisterStatus.CHECK_USER_NAME;
        step = 1;

        edtGetValue = findViewById(R.id.edtGetFastRegister);
        btnNext = findViewById(R.id.btnNextFastRegister);
        btnNext.setOnClickListener(btnNextClicked);
        progressBar = findViewById(R.id.processBarFastRegister);
        progressBar.setVisibility(View.GONE);
        tvStep = findViewById(R.id.tvStepFastRegister);
        tvStep.setText(String.format("%s %d / %d", getString(R.string.chu_buoc), 1, 5));

        user = new Wp_user();
        wp_user_bll = new Wp_user_BLL();

        ImageView imageViewLogo = findViewById(R.id.imgViewLogoFastRegister);
        imageViewLogo.setOnClickListener(imageViewLogoClicked);

        setLogoLogin_Hello();
    }

    private View.OnClickListener btnNextClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Not null
            String text = edtGetValue.getText().toString();
            if (text.isEmpty()){
                edtGetValue.setError(getString(R.string.chu_cho_nay_khac_rong));
                edtGetValue.requestFocus();
                return;
            }

            if (isLoading){
                Toast.makeText(FastRegister.this, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                return;
            }isLoading = true;
            progressBar.setVisibility(View.VISIBLE);

            //status
            switch (status){
                case CHECK_USER_NAME:{
                    checkUsername(text);
                    break;
                }
                case CHECK_EMAIL_ADDRESS:{
                    checkEmailAddress(text);
                    break;
                }
                case CHECK_FULL_NAME:{
                    checkFullName(text);
                    break;
                }
                case CHECK_PASSWORD:{
                    checkPassword(text);
                    break;
                }
                case CHECK_RETYPE_PASSWORD:{
                    checkRetypePassword(text);
                    break;
                }
                case FINISHED:{
                    finished();
                    break;
                }
            }
        }
    };

    private void checkUsername(final String userName){
        if ( checkInputHaveSpace(userName))
            return;

        if (userName.contains("@")){
            edtGetValue.setError(getString(R.string.chu_ten_nguoi_dung_khong_chua_a_cong));
            edtGetValue.requestFocus();
            setNotBusy();
            return;
        }

        String urlRequest = String.format(getString(R.string.url_wp_user), userName, userName);
        wp_user_bll.toArrayWp_users(urlRequest, new OnMyFinishListener<ArrayList<Wp_user>>() {
            @Override
            public void onFinish(ArrayList<Wp_user> result) {
                if (result.size() == 0){
                    //Username not exists.
                    user.setUser_login(userName);
                    changeContext(getString(R.string.chu_nhap_dia_chi_email));
                    status = FastRegisterStatus.CHECK_EMAIL_ADDRESS;
                    setNextStep();
                }else{
                    //Username exists.
                    edtGetValue.setError(getString(R.string.chu_ten_nguoi_dung_da_ton_tai));
                    edtGetValue.requestFocus();
                }

                setNotBusy();
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                setNotBusy();
                Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailAddress(final String emailAddress){
        if ( checkInputHaveSpace(emailAddress))
            return;

        if (!emailAddress.contains("@")){
            edtGetValue.setError(getString(R.string.chu_email_can_phai_chua_a_cong));
            edtGetValue.requestFocus();
            setNotBusy();
            return;
        }

        String urlRequest = String.format(getString(R.string.url_wp_user), emailAddress, emailAddress);
        wp_user_bll.toArrayWp_users(urlRequest, new OnMyFinishListener<ArrayList<Wp_user>>() {
            @Override
            public void onFinish(ArrayList<Wp_user> result) {
                if (result.size() == 0){
                    //Username not exists.
                    user.setUser_email(emailAddress);
                    changeContext(getString(R.string.chu_nhap_ten_day_du));
                    status = FastRegisterStatus.CHECK_FULL_NAME;
                    setNextStep();
                }else{
                    //Username exists.
                    edtGetValue.setError(getString(R.string.chu_email_da_ton_tai));
                    edtGetValue.requestFocus();
                }

                setNotBusy();
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                setNotBusy();
                Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFullName(String fName){
        user.setDisplay_name(fName);
        changeContext(getString(R.string.chu_nhap_mat_khau));
        setNotBusy();
        status = FastRegisterStatus.CHECK_PASSWORD;
        setNextStep();
    }

    private void checkPassword(String pass){
        if (checkInputHaveSpace(pass))
            return;

        user.setUser_pass(pass);
        changeContext(getString(R.string.chu_nhap_lai_mat_khau));
        setNotBusy();
        status = FastRegisterStatus.CHECK_RETYPE_PASSWORD;
        setNextStep();
    }

    private void checkRetypePassword(String rPass){
        if (checkInputHaveSpace(rPass))
            return;

        if (!rPass.equals(user.getUser_pass())){
            edtGetValue.setError(getString(R.string.chu_mat_khau_khong_khop));
        }else{
            status = FastRegisterStatus.FINISHED;
            setNextStep();
            setNotBusy();
            finished();
        }
        setNotBusy();
    }

    private void finished(){
        progressBar.setVisibility(View.GONE);
        final AlertDialog dialog = FunctionsStatic.createProcessDialog(this);
        FunctionsStatic.showDialog(dialog);
        user.setUser_registered(FunctionsStatic.getCurrentDayTime());
         wp_user_bll.addUser(getString(R.string.url_insert_user), user, new OnMyFinishListener<Boolean>() {
             @Override
             public void onFinish(Boolean result) {
                 if (result){
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

                                 Intent intent = new Intent(FastRegister.this, MainActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                 startActivity(intent);
                             }else
                                 Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                             FunctionsStatic.cancelDialog(dialog);
                             setNotBusy();
                         }

                         @Override
                         public void onError(Throwable error, Object bonusOfCoder) {
                             FunctionsStatic.cancelDialog(dialog);
                             setNotBusy();
                             Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
                 else{
                     FunctionsStatic.cancelDialog(dialog);
                     setNotBusy();
                     Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onError(Throwable error, Object bonusOfCoder) {
                 FunctionsStatic.cancelDialog(dialog);
                 setNotBusy();
                 Toast.makeText(FastRegister.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void changeContext(final String textHint){
        btnNext.setClickable(false);
        btnNext.animate().alpha(0).setDuration(500);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtGetValue.animate().translationY(10000).alpha(0).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    edtGetValue.setTranslationY(-10000);
                    edtGetValue.setText(null);
                    edtGetValue.setHint(textHint);
                    edtGetValue.animate().translationY(0).alpha(1).rotation(360 * 10).setDuration(500);
                    btnNext.animate().alpha(1).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setClickable(true);
                        }
                    });
                }
            });

        }else{
            Toast.makeText(this, "Android Not Support For Version 4.0", Toast.LENGTH_SHORT).show();
            edtGetValue.setTranslationY(-10000);
            edtGetValue.setText(null);
            edtGetValue.setHint(textHint);
            edtGetValue.animate().translationY(0).alpha(1).rotation(360 * 10).setDuration(500);
            btnNext.animate().alpha(1).setDuration(500);
            btnNext.setClickable(true);
        }
    }

    private boolean checkInputHaveSpace(String text){
        if (text.contains(" ")){
            edtGetValue.setError(getString(R.string.chu_khong_chua_khoang_trang));
            edtGetValue.requestFocus();
            setNotBusy();
            return true;
        }

        return false;
    }

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

    private void setNotBusy(){
        progressBar.setVisibility(View.GONE);
        isLoading = false;
    }

    private void setNextStep(){
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 5, RotateAnimation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (step < 5)
                    tvStep.setText(String.format(getString(R.string.chu_buoc)+" %d / %d", ++step, MAX_STEP));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvStep.startAnimation(rotateAnimation);
    }

    private void setLogoLogin_Hello(){
        final ImageView imgLogoLogin = findViewById(R.id.imgViewLogoFastRegister);
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

    private Wp_user user;

    private boolean isLoading;
    private FastRegisterStatus status;
    private int step;

    private Button btnNext;
    private EditText edtGetValue;
    private ProgressBar progressBar;
    private TextView tvStep;

    Wp_user_BLL wp_user_bll;
}