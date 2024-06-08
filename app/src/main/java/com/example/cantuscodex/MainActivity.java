package com.example.cantuscodex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.cantuscodex.data.users.model.User;

import com.google.android.material.navigation.NavigationView;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cantuscodex.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences mPreferences;
    private String sharedPrefsFile = "com.example.cantuscodex";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPreferences = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);


        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_songs, R.id.nav_events, R.id.nav_bookmarked_events)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem btnLogout = binding.navView.getMenu().findItem(R.id.nav_login);
        btnLogout.setOnMenuItemClickListener(item -> {
            mAuth.signOut();
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            preferencesEditor.clear();
            preferencesEditor.apply();
            navController.popBackStack(R.id.nav_login, false);
            navController.navigate(R.id.nav_login);
            return false;
        });



        View headerRoot = binding.navView.getHeaderView(0);
        TextView UserMail = headerRoot.findViewById(R.id.header_mail);
        TextView UserTitle = headerRoot.findViewById(R.id.header_titles);



        UserMail.setText(mPreferences.getString(User.FIELD_EMAIL, "Error"));

        if (mPreferences.getBoolean(User.FIELD_IS_ADMIN, false)){
            UserTitle.setText("Admin");
        }else {
            UserTitle.setText("User");
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key == User.FIELD_EMAIL) {
                    UserMail.setText(mPreferences.getString(User.FIELD_EMAIL, "Error"));
                }
                if (key == User.FIELD_IS_ADMIN) {
                    if (mPreferences.getBoolean(User.FIELD_IS_ADMIN, false)){
                        UserTitle.setText("Admin");
                    }else {
                        UserTitle.setText("User");
                    }
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




}