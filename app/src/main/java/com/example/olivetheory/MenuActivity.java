package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private ArrayAdapter<String> suggestAdapter;
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
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
        ImageButton logoutButton = findViewById(R.id.logout);
        ImageButton messageButton = findViewById(R.id.message);
        Button suggestVariety = findViewById(R.id.suggest);
        TextView problemsButton = findViewById(R.id.problemsText);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView maps = findViewById(R.id.mapsselection);
        ImageButton forumButton = findViewById(R.id.forum);
    }

    private void setButtonListeners() {
        findViewById(R.id.weatherText).setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        findViewById(R.id.settings).setOnClickListener(v -> startNewActivity(SettingsActivity.class));
        findViewById(R.id.user).setOnClickListener(v -> startNewActivity(UserProfile.class));
        findViewById(R.id.calendar).setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        findViewById(R.id.weather).setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        findViewById(R.id.problemsText).setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        findViewById(R.id.message).setOnClickListener(v -> startNewActivity(ChatListActivity.class));
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

        findViewById(R.id.logout).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LogOutActivity.class);
            startActivity(intent);
            finish();
        });
    }

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
                Log.d("loadOliveVarieties", "Loaded variety: " + line.trim());
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

        if (oliveVarieties == null || oliveVarieties.isEmpty()) {
            Toast.makeText(this, "Η λίστα ποικιλιών είναι άδεια ή δεν φορτώθηκε σωστά.", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        String randomVariety = oliveVarieties.get(random.nextInt(oliveVarieties.size()));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_variety, null);
        dialogBuilder.setView(dialogView);

        TextView varietyTextView = dialogView.findViewById(R.id.variety); // TextView to display the random variety
        Button closeButton = dialogView.findViewById(R.id.close);
        varietyTextView.setText("Προτεινόμενη ποικιλία ελιάς: " + randomVariety);

        AlertDialog alertDialog = dialogBuilder.create();

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}
