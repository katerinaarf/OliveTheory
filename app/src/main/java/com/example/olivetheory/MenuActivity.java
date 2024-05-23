package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize buttons
        Button settingButton = findViewById(R.id.settings);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton logoutButton = findViewById(R.id.logout);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogOutActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listeners for each button
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(SettingsActivity.class);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(CalendarActivity.class);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(WeatherActivity.class);
            }
        });

//        problemsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startNewActivity(ProblemsActivity.class);
//            }
//        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(MessageActivity.class);
            }
        });

        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ForumActivity.class);
            }
        });
    }

    // Method to start a new activity
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(MenuActivity.this, cls);
        startActivity(intent);
    }
}
