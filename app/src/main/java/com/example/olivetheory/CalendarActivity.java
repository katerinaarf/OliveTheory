package com.example.olivetheory;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private DatabaseReference workRef;
    private DatabaseReference suggestRef;
    private ArrayList<String> suggestWorkList;
    private ArrayAdapter<String> suggestAdapter;
    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // In the onCreate method of an activity that should only be accessible to farmers
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
                                    String userType = document.getString("userType");
                                    if (!"Αγρότης".equals(userType)) {
                                        Toast.makeText(CalendarActivity.this, "Η πρόσβαση σε αυτή τη σελίδα επιτρέπεται μόνο σε αγρότες.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }
                    });
        }
        setContentView(R.layout.activity_calendar);


        workRef = FirebaseDatabase.getInstance().getReference().child("work_history");
        suggestRef = FirebaseDatabase.getInstance().getReference().child("suggestions");

        ImageButton user = findViewById(R.id.user);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        ImageButton messageButton = findViewById(R.id.message);
        ImageButton forumButton = findViewById(R.id.forum);
        TextView historyButton = findViewById(R.id.history_work);


        TextView suggestButton = findViewById(R.id.suggest);

        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        problemsButton.setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        messageButton.setOnClickListener(v -> startNewActivity(ChatListActivity.class));
        forumButton.setOnClickListener(v -> startNewActivity(ForumListActivity.class));
        historyButton.setOnClickListener(v -> startNewActivity(HistoryWorkActivity.class));
        suggestButton.setOnClickListener(v -> suggestWork());

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                showAddWorkDialog(selectedDate);
            }
        });

    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(CalendarActivity.this, cls);
        startActivity(intent);
    }

    private void showAddWorkDialog(String date) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_work, null);
        dialogBuilder.setView(dialogView);

        EditText addwork = dialogView.findViewById(R.id.add_work_txt);
        TextView add = dialogView.findViewById(R.id.add);
        TextView cancel = dialogView.findViewById(R.id.cancel);
        AlertDialog alertDialog = dialogBuilder.create();

        add.setOnClickListener(v -> {
            String work = addwork.getText().toString();
            if (!work.isEmpty()) {
                if (date != null) {
                    workRef.child(date).push().setValue(work);
                    alertDialog.dismiss();
                } else {
                    workRef.push().setValue(work);
                }
                Toast.makeText(CalendarActivity.this, "Εργασία προστέθηκε", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CalendarActivity.this, "Το πεδίο δεν μπορεί να είναι κενό", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> alertDialog.cancel());

        alertDialog.show();
    }

    private void suggestWork() {
        suggestWorkList = new ArrayList<>();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_suggest, null);
        dialogBuilder.setView(dialogView);

        TextView suggestTextView = dialogView.findViewById(R.id.suggestText);
        TextView close = dialogView.findViewById(R.id.close);

        AlertDialog alertDialog = dialogBuilder.create();

        suggestWorkList.addAll(readSuggestedWorkFromJSON());

        suggestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String suggestion = snapshot.getValue(String.class);
                    suggestWorkList.add(suggestion);
                }
                if (!suggestWorkList.isEmpty()) {
                    String randomSuggestion = suggestWorkList.get((int) (Math.random() * suggestWorkList.size()));
                    suggestTextView.setText(randomSuggestion);
                } else {
                    suggestTextView.setText("No suggestions available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private ArrayList<String> readSuggestedWorkFromJSON() {
        ArrayList<String> suggestedWorkList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("olive.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray oliveArray = jsonObject.getJSONArray("olive");

            for (int i = 0; i < oliveArray.length(); i++) {
                JSONObject oliveObject = oliveArray.getJSONObject(i);
                String work = oliveObject.getString("work");
                suggestedWorkList.add(work);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return suggestedWorkList;
    }
}
