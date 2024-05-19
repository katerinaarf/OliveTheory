package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

//        //Text
//        TextView weatherText = findViewById(R.id.weatherText);
//        TextView problemsText = findViewById(R.id.problemsText);

        // Buttons
        Button settingButton = findViewById(R.id.settings);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        Button problemsButton = findViewById(R.id.problems);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);


        //Change Activity

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsActivity = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarActivity = new Intent(MenuActivity.this, CalendarActivity.class);
                startActivity(calendarActivity);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weatherActivity = new Intent(MenuActivity.this, WeatherActivity.class);
                startActivity(weatherActivity);
            }
        });

        problemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent problemsActivity = new Intent(MenuActivity.this, ProblemsActivity.class);
                startActivity(problemsActivity);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageActivity = new Intent(MenuActivity.this, MenuActivity.class);
                startActivity(messageActivity);
            }
        });

        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });
    }
}


