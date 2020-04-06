package com.example.minds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.HomeFragment;
import fragments.MyAccount;

public class CourseSelector extends AppCompatActivity {

    private Toolbar                 topMenu;
    private TextView                topMenuText;
    private BottomNavigationView    bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);

        initializeControllers();

        topMenuText.setText("Main Page");

//        openFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
                                        BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.listBtn :
                    topMenuText.setText("Main Page");
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.accountBtn :
                    topMenuText.setText("My Account");
                    selectedFragment = new MyAccount();
                    break;
            }
            openFragment(selectedFragment);
            return true;
        }
    };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void initializeControllers() {
        topMenu = (Toolbar) findViewById(R.id.topMenu);
        topMenuText = (TextView) findViewById(R.id.topMenu_text);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
    }
}
