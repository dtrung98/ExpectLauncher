package com.teamll.expectlauncher.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.fragments.AppDrawerFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new AppDrawerFragment())
                    .commitNow();
        }
    }
}
