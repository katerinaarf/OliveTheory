package com.example.olivetheory.gui.usermanagment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olivetheory.R;
import com.example.olivetheory.gui.mainactivities.MenuActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String USER_LOGGED_IN_KEY = "user_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean userLoggedIn = prefs.getBoolean(USER_LOGGED_IN_KEY, false);

        if (userLoggedIn) {
            redirectToMenuActivity();
        } else {
            redirectToSignUpActivityAfterDelay();
        }
    }

    private void redirectToSignUpActivityAfterDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void redirectToMenuActivity() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
