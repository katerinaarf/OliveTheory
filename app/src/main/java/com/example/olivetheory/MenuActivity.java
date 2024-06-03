package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        oliveVarieties = loadOliveVarieties();

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userType = document.getString("userType");
                            } else {
                                userType = "Unknown";
                            }
                        } else {
                            userType = "Unknown";
                        }
                    });
        } else {
            userType = "Unknown";
        }

        initializeUI();
        setButtonListeners();
    }

    private void initializeUI() {
        TextView weather = findViewById(R.id.weatherText);
        ImageButton settingButton = findViewById(R.id.settings);
        ImageButton user = findViewById(R.id.user);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton weatherButton = findViewById(R.id.weather);
        ImageButton problems = findViewById(R.id.problems);
        ImageButton messageButton = findViewById(R.id.message);
        TextView suggestVariety = findViewById(R.id.suggest);
        TextView problemsButton = findViewById(R.id.problemsText);
        TextView maps = findViewById(R.id.mapsselection);
        ImageButton forumButton = findViewById(R.id.forum);

        // Προσθέστε λογική για το άνοιγμα των ρυθμίσεων
        settingButton.setOnClickListener(v -> toggleSettingsFragment());
    }

    private void setButtonListeners() {
        findViewById(R.id.weatherText).setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        findViewById(R.id.settings).setOnClickListener(v -> toggleSettingsFragment());
        findViewById(R.id.user).setOnClickListener(v -> startNewActivity(UserProfile.class));
        findViewById(R.id.calendar).setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        findViewById(R.id.weather).setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        findViewById(R.id.problemsText).setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        findViewById(R.id.message).setOnClickListener(v -> startNewActivity(ChatListActivity.class));
        findViewById(R.id.problems).setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        findViewById(R.id.forum).setOnClickListener(v -> startNewActivity(ForumListActivity.class));
        findViewById(R.id.suggest).setOnClickListener(v -> {
            if ("Γεωπόνος".equals(userType)) {
                Toast.makeText(MenuActivity.this, "Οι γεωπόνοι δεν έχουν πρόσβαση στην πρόταση ποικιλίας.", Toast.LENGTH_SHORT).show();
            } else if ("Αγρότης".equals(userType)) {
                suggestVariety();
            } else {
                Toast.makeText(MenuActivity.this, "Άγνωστος τύπος χρήστη.", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.mapsselection).setOnClickListener(v -> startNewActivity(MapsActivity.class));
    }

    private void toggleSettingsFragment() {
        FrameLayout settingsFragmentContainer = findViewById(R.id.settings_fragment_container);
        if (settingsFragmentContainer.getVisibility() == View.GONE) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_fragment_container, new SettingsActivity())
                    .commit();
            settingsFragmentContainer.setVisibility(View.VISIBLE);
        } else {
            settingsFragmentContainer.setVisibility(View.GONE);
        }
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(MenuActivity.this, cls);
        startActivity(intent);
    }

    private void suggestVariety() {
        Random random = new Random();
        int randomIndex = random.nextInt(oliveVarieties.size());
        String randomVariety = oliveVarieties.get(randomIndex);
        Toast.makeText(MenuActivity.this, "Προτεινόμενη Ποικιλία: " + randomVariety, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> loadOliveVarieties() {
        ArrayList<String> varieties = new ArrayList<>();
        String rawData = "Κορωνέικη\nΚαλαμών\nΑμφίσσης\nΜεγαρίτικη\nΑδραμυττινή\nΤσουνάτη\nΧαλκιδικής\nΘρουμπολιά\nΜανάκι\nΛιανολιά\nΚονσερβολιά";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                varieties.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return varieties;
    }
}
