package com.nhom08.doanlaptrinhandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init(){
        //get setting shared
        SharedPreferences sp = getSharedPreferences("myHufierSetting", MODE_PRIVATE);
        langOld = sp.getString("myHufierLang", "vi");
        postNumOld = sp.getInt("myHufierPostNum", 50);
        isDarkModeOld = sp.getBoolean("isDarkMode", false);


        Switch aSwitchNightMode = findViewById(R.id.switchNightModeSetting);
        aSwitchNightMode.setChecked(isDarkModeOld);

        Button btnSave = findViewById(R.id.btnSaveSetting),
        btnCancel = findViewById(R.id.btnCancelSetting);

        RadioButton rdLangVi = findViewById(R.id.radioButtonLangViSetting),
                rdLangEn = findViewById(R.id.radioButtonLangEngSetting);
        if (langOld.equals("vi"))
            rdLangVi.setChecked(true);
        else
            rdLangEn.setChecked(true);

        EditText edtNumPost = findViewById(R.id.edtNumberPostsSetting);
        edtNumPost.setText(String.valueOf(postNumOld));

        btnCancel.setOnClickListener(btnCancelClicked);
        btnSave.setOnClickListener(btnSaveClicked);

        aSwitchNightMode.setOnCheckedChangeListener(aSwitchNightModeCheckedChange);
    }

    private CompoundButton.OnCheckedChangeListener aSwitchNightModeCheckedChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //isDarkModeOld = isChecked;
        }
    };

    private View.OnClickListener btnCancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Setting.this.finish();
        }
    };

    private View.OnClickListener btnSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText edtNumPost = findViewById(R.id.edtNumberPostsSetting);
            String strNum = edtNumPost.getText().toString();
            if (strNum.isEmpty()){
                edtNumPost.setError(getString(R.string.chu_cho_nay_khac_rong));
                edtNumPost.requestFocus();
                return;
            }

            //condition 1
            int num = Integer.parseInt(strNum);

            //condition 2
            RadioButton radioButtonLangVi = findViewById(R.id.radioButtonLangViSetting);

            //condition 3
            Switch sw = findViewById(R.id.switchNightModeSetting);
            boolean isDark = sw.isChecked();

            if ((num != postNumOld) || ((langOld.equals("vi") && !radioButtonLangVi.isChecked())) || (langOld.isEmpty() && radioButtonLangVi.isChecked()) || isDarkModeOld != isDark){
                //save sharedPreferences
                SharedPreferences sp = getSharedPreferences("myHufierSetting", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("myHufierLang",radioButtonLangVi.isChecked() ? "vi" : "");
                editor.putInt("myHufierPostNum", num);
                editor.putBoolean("isDarkMode", isDark);
                editor.apply();

                //reload Request
                FunctionsStatic.hienThiThongBaoDialog(Setting.this, getString(R.string.chu_thong_bao), getString(R.string.chu_cai_dat_thanh_cong_yeu_cau_khoi_dong_lai), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Setting.this.finish();
                        dialog.cancel();
                    }
                });
            }else{
                FunctionsStatic.hienThiThongBaoDialog(Setting.this, getString(R.string.chu_thong_bao), getString(R.string.chu_hanh_dong_khong_chap_nhan));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setTitle(R.string.chu_cai_dat);
        return super.onCreateOptionsMenu(menu);
    }

    private String langOld;
    private int postNumOld;
    private boolean isDarkModeOld;
}