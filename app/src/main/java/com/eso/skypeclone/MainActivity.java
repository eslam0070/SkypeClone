package com.eso.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.replaceF,new HomeFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.navigation_settings:
                replaceFragment(new SettingsFragment());
                break;
            case R.id.navigation_notifications:
                replaceFragment(new NotificationsFragment());
                break;
            case R.id.navigation_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.replaceF, fragment).commit();
    }
}
