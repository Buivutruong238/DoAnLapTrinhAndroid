package com.nhom08.doanlaptrinhandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_post_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyUpdateProgress;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.adapter.ModuleForDevAdapter;

import java.util.ArrayList;

public class ModuleForDev extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moduls_for_dev);

        init();
    }

    private void init(){
        recyclerView = findViewById(R.id.recyclerViewModulsForDev);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wp_post_bll = new Wp_post_BLL();
        processDialog = FunctionsStatic.createProcessDialog(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshModuleForDev);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshLayoutRefresh);

        getTerm_id();
        initRecycleView();
    }

    private void initRecycleView(ArrayList<Wp_post> posts){
        adapter = new ModuleForDevAdapter(ModuleForDev.this, posts);
        recyclerView.setAdapter(adapter);
    }

    private void initRecycleView(){
        if (!swipeRefreshLayout.isRefreshing())
            FunctionsStatic.showDialog(processDialog);

        wp_post_bll.toArrayWp_posts(getString(R.string.url_wp_posts_term)+term_id, new OnMyFinishListener<ArrayList<Wp_post>>() {
            @Override
            public void onFinish(ArrayList<Wp_post> posts) {
                adapter = new ModuleForDevAdapter(ModuleForDev.this, posts);
                recyclerView.setAdapter(adapter);
                FunctionsStatic.cancelDialog(processDialog);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.cancelDialog(processDialog);
                FunctionsStatic.hienThiThongBaoDialog(ModuleForDev.this, getString(R.string.chu_thong_bao), getString(R.string.chu_ket_noi_het_thoi_gian_kiem_tra_lai_ket_noi_internet));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.chu_developer));
        searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnSearchClickListener(searchViewClicked);
        searchView.setOnQueryTextListener(searchViewQuerySubmit);
        actionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    private SearchView.OnQueryTextListener searchViewQuerySubmit = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.filter(newText);
            return true;
        }
    };

    private View.OnClickListener searchViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ModuleForDev.this);
            builder.setTitle(R.string.chu_thong_bao);
            builder.setMessage(R.string.chu_ban_muon_loc_hay_tim_kim);
            builder.setPositiveButton(R.string.chu_loc, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    searchView.setQueryHint(getString(R.string.chu_nhap_de_loc));
                }
            });
            builder.setNegativeButton(R.string.chu_tim_bat_cu_dau, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    searchView.clearFocus();
                    AlertDialog.Builder builderSearch = new AlertDialog.Builder(ModuleForDev.this);
                    builderSearch.setTitle(R.string.chu_tim_bat_cu_dau);
                    builderSearch.setView(ModuleForDev.this.getLayoutInflater().inflate(R.layout.layout_search_anywhere, null));
                    dialog.cancel();
                    showSearchViewPopup(builderSearch.create());
                }
            });
            builder.create().show();
        }
    };

    private void showSearchViewPopup(final AlertDialog alertDialog){
        alertDialog.setCancelable(false);
        alertDialog.show();

        //get view
        final EditText edtEnterKey = alertDialog.findViewById(R.id.edtEnterKeyToSearch);
        SeekBar seekBarDoChinhXac = alertDialog.findViewById(R.id.seekBarDoChinhXacSearchAnywhere);
        final TextView tvDoChinhXac = alertDialog.findViewById(R.id.tvChonDoChinhXacSearchAnywhere);
        final ProgressBar progressBarSumPosts = alertDialog.findViewById(R.id.progressBarTinhToanSumPosts);
        final ProgressBar progressBarLoading = alertDialog.findViewById(R.id.progressBarSearchAnywhere);
        final TextView tvTinhTong = alertDialog.findViewById(R.id.tvSumPostsSearchAnywhere);
        final TextView tvLoading = alertDialog.findViewById(R.id.tvLoadingPercentSearchAnywhere);
        final TextView tvTimThay = alertDialog.findViewById(R.id.tvTimThayKetQuaSearchAnyWhere);
        Button btnCancel = alertDialog.findViewById(R.id.btnCancelSeachAnyWhere);
        Button btnSearch = alertDialog.findViewById(R.id.btnTimKiemSearchAnywhere);

        //init component.
        final int MAX_SEEKBAR = 4;
        final int[] DO_CHINH_XAC = {MAX_SEEKBAR * 100 / MAX_SEEKBAR};
        seekBarDoChinhXac.setMax(MAX_SEEKBAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBarDoChinhXac.setProgress(MAX_SEEKBAR, true);
        }else
            seekBarDoChinhXac.setProgress(4);

        tvDoChinhXac.setText(getString(R.string.chon_do_chinh_xac));

        progressBarSumPosts.setVisibility(View.GONE);

        tvLoading.setText(getString(R.string.loading_not));

        tvTimThay.setText(getString(R.string.tim_thay_0));

        //set seekbar change progress.
        seekBarDoChinhXac.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DO_CHINH_XAC[0] = progress * 100 / MAX_SEEKBAR;
                tvDoChinhXac.setText(String.format(getString(R.string.chon_do_chinh_xac_percent), DO_CHINH_XAC[0]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //set btnClicked
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                searchView.clearFocus();
            }
        });

        final boolean[] isBusy = {false};
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBusy[0]) {//is busy?
                    Toast.makeText(ModuleForDev.this, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                    return;
                }

                //key empty?
                final String keySearch = edtEnterKey.getText().toString();
                if (keySearch.isEmpty()){
                    edtEnterKey.setError(getString(R.string.chu_cho_nay_khac_rong));
                    edtEnterKey.requestFocus();
                    return;
                }

                //show process sum posts.
                isBusy[0] = true;
                progressBarSumPosts.setVisibility(View.VISIBLE);
                wp_post_bll.toArrayWp_posts(getString(R.string.url_wp_posts_term)+term_id, new OnMyFinishListener<ArrayList<Wp_post>>() {
                    @Override
                    public void onFinish(ArrayList<Wp_post> result) {
                        //finish sum posts;
                        int sumPost = result.size();
                        progressBarSumPosts.setVisibility(View.GONE);
                        tvTinhTong.setText(String.format(getString(R.string.trang_thai_tong_posts_items), sumPost));
                        progressBarLoading.setMax(sumPost);
                        progressBarLoading.setProgress(0);
                        tvTimThay.setText(getString(R.string.tim_thay_0));

                        //Find Items By Key In background.
                        wp_post_bll.findItems(result, keySearch, DO_CHINH_XAC[0], new OnMyFinishListener<ArrayList<Wp_post>>() {
                            @Override
                            public void onFinish(ArrayList<Wp_post> result) {
                                initRecycleView(result);
                                isBusy[0] = false;
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                Toast.makeText(ModuleForDev.this, "Error: " + bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
                                isBusy[0] = false;
                            }
                        }, new OnMyUpdateProgress<Integer, Integer>() {
                            @Override
                            public void onUpdateProgress(final Integer percent) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBarLoading.setProgress(percent+1);
                                        tvLoading.setText(String.format(getString(R.string.loading_percent), percent+1));
                                    }
                                });
                            }

                            @Override
                            public void onUpdateValue(final Integer integer) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvTimThay.setText(getString(R.string.tim_thay)+integer);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error, Object bonusOfCoder) {
                        Toast.makeText(ModuleForDev.this, "Error: "+bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
                        isBusy[0] = false;
                    }
                });
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            goHome();
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(ModuleForDev.this, Setting.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome(){
        Intent intent = new Intent(ModuleForDev.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getTerm_id(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            term_id = bundle.getInt("term_id", 42);
        else
            term_id = 42;
    }

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshLayoutRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initRecycleView();
        }
    };

    private RecyclerView recyclerView;
    private ModuleForDevAdapter adapter;
    private Wp_post_BLL wp_post_bll;
    private AlertDialog processDialog;
    private int term_id;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
}
