package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class MenuActivity extends AppCompatActivity {
    private ArrayList<String> oliveVarieties;
    private ArrayAdapter<String> suggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loadOliveVarieties();
        TextView weather = findViewById(R.id.weatherText);

        // Initialize buttons
        Button settingButton = findViewById(R.id.settings);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton logoutButton = findViewById(R.id.logout);
        Button messageButton = findViewById(R.id.message);
        Button suggestVariety = findViewById(R.id.suggest);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView maps = findViewById(R.id.mapsselection);
        Button forumButton = findViewById(R.id.forum);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogOutActivity.class);
                startActivity(intent);
                finish();
            }
        });


        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(WeatherActivity.class);
            }
        });

        // Set click listeners for each button
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(SettingsActivity.class);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UserProfile.class);
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
                startNewActivity(ChatListActivity.class);
            }
        });

        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ForumListActivity.class);
            }
        });

        suggestVariety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestVariety();
            }
        });
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(MapsActivity.class);
            }
        });
    }

    // Method to start a new activity
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(MenuActivity.this, cls);
        startActivity(intent);
    }
    private ArrayList<String> loadOliveVarieties() {
        ArrayList<String> oliveVarieties = new ArrayList<>();
        try {
            InputStream is = getAssets().open("varieties.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String content = new String(buffer, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
            String line;
            while ((line = reader.readLine()) != null) {
                oliveVarieties.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oliveVarieties;
    }


    private void suggestVariety() {
        oliveVarieties = loadOliveVarieties();
        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, oliveVarieties);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_variety, null);
        dialogBuilder.setView(dialogView);

        ListView varietyListView = dialogView.findViewById(R.id.variety);
        Button closeButton = dialogView.findViewById(R.id.close);
        Button suggestButton = dialogView.findViewById(R.id.choice);
        varietyListView.setAdapter(suggestAdapter);

        AlertDialog alertDialog = dialogBuilder.create();

        suggestButton.setOnClickListener(v -> {
            if (!oliveVarieties.isEmpty()) {
                Random random = new Random();
                String randomVariety = oliveVarieties.get(random.nextInt(oliveVarieties.size()));

                // Εμφανίζουμε την τυχαία επιλεγμένη ποικιλία
                Toast.makeText(this, "Προτεινόμενη ποικιλία ελιάς: " + randomVariety, Toast.LENGTH_SHORT).show();

                // Εδώ μπορείτε να προσθέσετε λογική για να αποθηκεύσετε την επιλεγμένη ποικιλία στη βάση δεδομένων
                // Παράδειγμα:
                // saveVarietyToDatabase(randomVariety);
            } else {
                Toast.makeText(this, "Η λίστα ποικιλιών είναι άδεια.", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.setView(dialogView);
        alertDialog.show();
    }


}