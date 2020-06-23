package com.nhom08.doanlaptrinhandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_term_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_term;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //initial component
        init();

        //thong bao
        thongBaoKhiCoBaiViet(null, 3000);

        //test update volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://vietsacloit.000webhostapp.com/android_app/url_update_like_post.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("post_id", "49");
                map.put("user_id", "2");
                map.put("like", "100");

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void init(){
        //initial variable
        wp_term_bll = new Wp_term_BLL();
        processDialog = FunctionsStatic.createProcessDialog(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        loadMenuNavigationView(navigationView.getMenu());
    }

    private void loadMenuNavigationView(final Menu menu){
        menu.clear();
        wp_term_bll.toArrayWp_terms(getString(R.string.url_wp_terms), new OnMyFinishListener<ArrayList<Wp_term>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(ArrayList<Wp_term> result) {
                menu.add(0, 42,0,R.string.chu_module_for_dev).setIcon(R.drawable.ic_widgets_black_24dp);
                SubMenu subMenu1 = menu.addSubMenu(getString(R.string.top_category));
                menuItemChecked = subMenu1.add(1, 0, 0, getString(R.string.new_posts)).setIcon(R.drawable.ic_menu_news_post).setChecked(true);
                //For other category
                ArrayList<Wp_term> otherCates = new ArrayList<>();

                for(Wp_term term : result) {
                    int id = term.getTerm_id();
                    String name = term.getName();
                    switch (id){
                        case 42:break;//module for dev was added

                        case 3: {
                            MenuItem menuItem = subMenu1.add(1, id, 0, name);
                            menuItem.setIcon(R.drawable.ic_menu_hufi_exam);
                            break;
                        }
                        case 4: {
                            MenuItem menuItem = subMenu1.add(1, id, 0, name);
                            menuItem.setIcon(R.drawable.ic_menu_khoa_hoc);
                            break;
                        }
                        case 5: {
                            MenuItem menuItem = subMenu1.add(1, id, 0, name);
                            menuItem.setIcon(R.drawable.ic_menu_0xdhth);
                            break;
                        }
                        case 30: {
                            MenuItem menuItem = subMenu1.add(1, id, 0, name);
                            menuItem.setIcon(R.drawable.ic_menu_source_code);
                            break;
                        }
                        default: {
                            otherCates.add(term);
                            break;
                        }
                    }
                }

                //add other category if found!
                SubMenu subMenu2 = menu.addSubMenu(getString(R.string.other_category));
                for(Wp_term term : otherCates){
                    int id = term.getTerm_id();
                    String name = term.getName();
                    if (id == 1)
                        subMenu2.add(2, id, 0, name.toUpperCase()).setIcon(R.drawable.ic_menu_not_category);
                    else
                        subMenu2.add(2, id, 0, name).setIcon(R.drawable.ic_menu_category);
                }

                SubMenu subMenu3 = menu.addSubMenu(getString(R.string.app_tools));
                subMenu3.add(3, R.string.navActionLogout, 0, getString(R.string.chu_dang_xuat)).setIcon(R.drawable.ic_menu_logout);
                subMenu3.add(3, R.string.navActionLogin, 0, getString(R.string.chu_dang_nhap)).setIcon(R.drawable.ic_menu_login);
                subMenu3.add(3, R.string.navActionRegister, 0, getString(R.string.chu_dang_ky)).setIcon(R.drawable.ic_menu_register);

                FunctionsStatic.cancelDialog(processDialog);

                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.cancelDialog(processDialog);
                Toast.makeText(MainActivity.this, "LOAD MENU ERROR: "+bonusOfCoder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id < 0)
            return false;

        if(id == R.string.navActionLogout){
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.string.navActionLogin){
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.string.navActionRegister){
            Toast.makeText(this, "Register", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == 42) {//module for dev
            Toast.makeText(this, "Module For Dev", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (menuItemChecked != null)
            menuItemChecked.setChecked(false);

        item.setChecked(true);
        menuItemChecked = item;

        if (id == 0)
            Toast.makeText(this, "Update Default", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Update Post of term", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Thu code phan thong bao khi co bai viet
    private void thongBaoKhiCoBaiViet(List<Wp_post> listHienTai, final int milisec){
        final Handler handler = new Handler();
        final int[] i = {0};
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //len
                TextView textView = findViewById(R.id.tvThongBaoBaiVietMoi);
                textView.setText("thong bao: "+ i[0]++);
                handler.postDelayed(this, milisec);
            }
        }, milisec);
    }

    private Wp_term_BLL wp_term_bll;
    private MenuItem menuItemChecked;
    private AlertDialog processDialog;
}
