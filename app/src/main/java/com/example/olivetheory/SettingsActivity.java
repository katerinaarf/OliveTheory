package com.example.olivetheory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        // Buttons
        ImageButton user = findViewById(R.id.user);
        ImageButton settingButton = findViewById(R.id.settings);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        ImageButton messageButton = findViewById(R.id.message);
        ImageButton forumButton = findViewById(R.id.forum);

        TextView usertext = findViewById(R.id.usertext);
        TextView location = findViewById(R.id.location);
        TextView history_work = findViewById(R.id.history_work);
        TextView center_info = findViewById(R.id.center_info);
        //Change Activity

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UserProfile.class);
            }
        });

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

        problemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ProblemsActivity.class);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ChatListActivity.class);
            }
        });

        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ForumListActivity.class);
            }
        });

        usertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UserProfile.class);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(Location.class);
            }
        });

        history_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(HistoryWorkActivity.class);
            }
        });

        center_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(InfoActivity.class);
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(SettingsActivity.this, cls);
        startActivity(intent);
    }
}


