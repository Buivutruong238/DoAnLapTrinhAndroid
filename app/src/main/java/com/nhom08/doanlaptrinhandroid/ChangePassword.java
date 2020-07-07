package com.nhom08.doanlaptrinhandroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
    }

    private void init(){
        edtPass = findViewById(R.id.edtPassChangePassword);
        edtRetypePass = findViewById(R.id.edtRetypePasschangePassword);
        btnChangePass = findViewById(R.id.btnChangePassword);
        processDialog = FunctionsStatic.createProcessDialog(this);

        btnChangePass.setOnClickListener(btnChangePassClicked);
    }

    private View.OnClickListener btnChangePassClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //check bundle?
            Bundle bundle = getIntent().getExtras();
            if (bundle == null)
                ChangePassword.this.finish();
            String user_email = bundle.getString("user_email", null);
            if (user_email == null || !user_email.contains("@"))
                ChangePassword.this.finish();

            String newPass = edtPass.getText().toString(),
                    newRetypePass = edtRetypePass.getText().toString();
            //pass empty?
            if (newPass.isEmpty()){
                edtPass.setError(getString(R.string.chu_cho_nay_khac_rong));
                edtPass.requestFocus();
                return;
            }

            //pass math?
            if (!newPass.equals(newRetypePass)){
                edtRetypePass.setError(getString(R.string.chu_mat_khau_khong_khop));
                edtRetypePass.requestFocus();
                return;
            }

            //update password.
            Wp_user newUser = new Wp_user();
            newUser.setUser_email(user_email);
            newUser.setUser_pass(FunctionsStatic.hashMD5(newPass));
            Wp_user_BLL wp_user_bll = new Wp_user_BLL();
            String strUrl = String.format(getString(R.string.url_update_user_password), newUser.getUser_email(), newUser.getUser_pass());
            FunctionsStatic.showDialog(processDialog);
            wp_user_bll.updatePassWord(strUrl, newUser, new OnMyFinishListener<Boolean>() {
                @Override
                public void onFinish(Boolean result) {
                    FunctionsStatic.cancelDialog(processDialog);
                    if (result){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setTitle(R.string.chu_thanh_cong);
                        builder.setMessage(R.string.chu_mat_khau_cua_ban_da_thay_doi);
                        builder.setPositiveButton(R.string.chu_dang_nhap, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ChangePassword.this, Login.class);
                                startActivity(intent);
                                ChangePassword.this.finish();
                            }
                        });
                        builder.setNegativeButton(R.string.chu_thoat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ChangePassword.this.finish();
                            }
                        });builder.create().show();
                    }else{
                        Toast.makeText(ChangePassword.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.cancelDialog(processDialog);
                    Toast.makeText(ChangePassword.this, R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.chu_doi_mat_khau).toUpperCase());
        return super.onCreateOptionsMenu(menu);
    }

    private EditText edtPass, edtRetypePass;
    private Button btnChangePass;
    private AlertDialog processDialog;
}
