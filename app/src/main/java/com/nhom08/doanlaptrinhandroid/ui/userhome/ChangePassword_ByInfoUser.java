package com.nhom08.doanlaptrinhandroid.ui.userhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.MainActivity;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword_ByInfoUser extends AppCompatActivity {

    private EditText edtOldPass, edtNewPass, edtRetypeNewPass;
    private Button btnChange, btnCancel;
    private AlertDialog processDialog;
    String user_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password__by_info_user);

        init();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword_ByInfoUser.this.finish();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //check bundle?
                Bundle bundle = getIntent().getExtras();
                if (bundle == null)
                    ChangePassword_ByInfoUser.this.finish();
                user_email = bundle.getString("user_email_ByInfoUser", null);
                if (user_email == null || !user_email.contains("@"))
                    ChangePassword_ByInfoUser.this.finish();

                //check old password
                if (!checkOldPass(edtOldPass.getText().toString(), v.getContext()))
                {
                    edtOldPass.setError(getString(R.string.sai_password));
                    edtOldPass.requestFocus();
                    return;
                }


                final String newPass = edtNewPass.getText().toString(),
                        newRetypePass = edtRetypeNewPass.getText().toString();
                //pass empty?
                if (newPass.isEmpty()) {
                    edtNewPass.setError(getString(R.string.chu_cho_nay_khac_rong));
                    edtNewPass.requestFocus();
                    return;
                }

                //pass math?
                if (!newPass.equals(newRetypePass)) {
                    edtRetypeNewPass.setError(getString(R.string.chu_mat_khau_khong_khop));
                    edtRetypeNewPass.requestFocus();
                    return;
                }

                //update password.
                final Wp_user newUser = new Wp_user();
                newUser.setUser_email(user_email);
                newUser.setUser_pass(FunctionsStatic.hashMD5(newPass));

                Wp_user_BLL wp_user_bll = new Wp_user_BLL();
                RequestQueue requestQueue = Volley.newRequestQueue(ChangePassword_ByInfoUser.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.url_update_user_password),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                SharedPreferences sp = v.getContext().getSharedPreferences("myHufierLogin", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();
                                ChangePassword_ByInfoUser.this.finish();
                                Intent intent = new Intent(ChangePassword_ByInfoUser.this, MainActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChangePassword_ByInfoUser.this, "Loi", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("user_email", newUser.getUser_email());
                        map.put("user_pass", newUser.getUser_pass());
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private void init() {
        edtOldPass = findViewById(R.id.edtPassOldChangePasswordByInfoUser);
        edtNewPass = findViewById(R.id.edtPassChangePasswordByInfoUser);
        edtRetypeNewPass = findViewById(R.id.edtRetypePasschangePasswordByInfoUser);
        btnChange = findViewById(R.id.btnChangePasswordByInfoUser);
        btnCancel = findViewById(R.id.btnCancel);
        processDialog = FunctionsStatic.createProcessDialog(this);
    }

    private boolean checkOldPass(String pass_cu_nhap, Context context) {
        final Wp_user userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(context);
        String passmahoa = FunctionsStatic.hashMD5(pass_cu_nhap);
        if (passmahoa.equals(userWasLogin.getUser_pass())) {
            return true;
        }
        return false;
    }
}
