package com.nhom08.doanlaptrinhandroid;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.nhom08.doanlaptrinhandroid.dto.Wp_term;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import vietsaclo.android.lib._MyFunctionsStatic;

public class GiaoDienChinhActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giao_dien_chinh);
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

        //initial
        init(navigationView.getMenu());
    }

    private void init(final Menu menu){
        progressDialog = _MyFunctionsStatic.createProcessDialog(this);

        factory = new ViewModelProvider.AndroidViewModelFactory(this.getApplication());
        giaoDienChinhActivityModel = new ViewModelProvider(this, factory).get(GiaoDienChinhActivityModel.class);

        giaoDienChinhActivityModel.getWp_terms_toList().observe(this, new Observer<List<Wp_term>>() {
            @Override
            public void onChanged(List<Wp_term> wp_terms) {
                Wp_term term;
                for (int i = 0;i< wp_terms.size(); i++){
                    term = wp_terms.get(i);
                    menu.add(0, term.getTerm_id(), 0, term.getName());
                }

                _MyFunctionsStatic.cancelDialog(progressDialog);
            }
        });

        _MyFunctionsStatic.showDialog(progressDialog);
        giaoDienChinhActivityModel.setMutableLiveData_byURL(getString(R.string.url_wp_terms));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.giao_dien_chinh, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Toast.makeText(this, "ID: "+id, Toast.LENGTH_SHORT).show();

        return true;
    }

    //Places Variable
    private GiaoDienChinhActivityModel giaoDienChinhActivityModel;
    private ViewModelProvider.AndroidViewModelFactory factory;
    private AlertDialog progressDialog;
}
