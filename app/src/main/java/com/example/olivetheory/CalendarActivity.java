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

public class CalendarActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

//        //Text
//        TextView weatherText = findViewById(R.id.weatherText);
//        TextView problemsText = findViewById(R.id.problemsText);

        // Buttons
        Button settingButton = findViewById(R.id.calendar_second);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);


        //Change Activity

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarsecActivity = new Intent(CalendarActivity.this, CalendarActivity.class);
                startActivity(calendarsecActivity);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarActivity = new Intent(CalendarActivity.this, CalendarActivity.class);
                startActivity(calendarActivity);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weatherActivity = new Intent(CalendarActivity.this, WeatherActivity.class);
                startActivity(weatherActivity);
            }
        });

        problemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent problemsActivity = new Intent(CalendarActivity.this, ProblemsActivity.class);
                startActivity(problemsActivity);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageActivity = new Intent(CalendarActivity.this, MessageActivity.class);
                startActivity(messageActivity);
            }
        });

        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forumActivity = new Intent(CalendarActivity.this, ForumActivity.class);
                startActivity(forumActivity);
            }
        });
    }
}


