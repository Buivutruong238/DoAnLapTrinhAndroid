package com.nhom08.doanlaptrinhandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nhom08.doanlaptrinhandroid.Interface_enum.KindStatusRunning;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

public class ScreenWaitLogo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_wait_logo);

        init();
    }

    @SuppressLint("ResourceType")
    private void init(){
        btnReload = findViewById(R.id.btnReloadScreenWait);
        btnReload.setOnClickListener(btnReloadClicked);
        btnReload.setText(R.string.chu_dang_tai_du_lieu);
        ImageView imgLogo = findViewById(R.id.imageViewLogoScreenWait);
        imgLogo.setImageResource(R.raw.logo_app_myhufier);
        reLoad();
    }

    private void reLoad(){
        final int MAXLOAD = FunctionsStatic.TIME_FOR_WAIT_DO_IN_BACKGROUND_20S;

        btnReload.setClickable(false);
        btnReload.animate().alpha(0).setDuration(2000);
        ProgressBar progressBar = findViewById(R.id.processBarScreenWaitLogo);
        progressBar.setVisibility(View.VISIBLE);
        final TextView tvError = findViewById(R.id.tvShowErrorScreenWaitLogo);
        tvError.setVisibility(View.GONE);
        tvError.animate().alpha(0).setDuration(2000);
        final ProgressBar progressBarHrz = findViewById(R.id.progressBarHozScreenWaitLogo);
        final TextView tvTimeLeft = findViewById(R.id.tvTimeLeftScreenWaitLogo);
        progressBarHrz.setMax(MAXLOAD);
        progressBarHrz.setProgress(MAXLOAD);
        progressBarHrz.setAlpha(0);
        tvTimeLeft.setAlpha(0);
        progressBarHrz.animate().alpha(1).setDuration((MAXLOAD * 2 / 3) * 1000);
        tvTimeLeft.animate().alpha(1).setDuration((MAXLOAD * 2 / 3) * 1000);

        //update and listening every 200ms
        final int[] count = {0};// count = 10 => 10s => connect bad or network not connected.
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int percentLoad;

                String status = KindStatusRunning.LOADING_DATA.toString();
                final SharedPreferences sp = getSharedPreferences("@myHufierLoading", MODE_PRIVATE);
                status = sp.getString("status", status);
                if (status.equals(KindStatusRunning.FINISH_LOADING_DATA.toString())){
                    // update to nothing. and destroy
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("status", KindStatusRunning.NO_THING.toString());
                            editor.apply();
                            ScreenWaitLogo.this.finish();
                        }
                    }, 500);

                }else if (count[0] < MAXLOAD){
                    //continue update and listening every 200ms
                    handler.postDelayed(this, 1000);
                    ++count[0];
                    percentLoad = MAXLOAD - count[0];

                    progressBarHrz.setProgress(percentLoad);
                    tvTimeLeft.setText(String.format("%d s Left.", percentLoad));
                }else{
                    ProgressBar progressBar = findViewById(R.id.processBarScreenWaitLogo);
                    progressBar.setVisibility(View.GONE);
                    TextView tvError = findViewById(R.id.tvShowErrorScreenWaitLogo);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.animate().alpha(1).setDuration(2000);
                    btnReload.setClickable(true);
                    btnReload.animate().alpha(1).setDuration(2000);
                    btnReload.setText(R.string.chu_tai_lai_du_lieu);
                    tvTimeLeft.setText(R.string.chu_het_thoi_gian);
                    tvTimeLeft.animate().rotation(360 * 10).setDuration(2000);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private View.OnClickListener btnReloadClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    };

    private Button btnReload;
}