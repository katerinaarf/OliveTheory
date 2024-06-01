package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;

public class HistoryWorkActivity extends AppCompatActivity {

    private DatabaseReference workHistoryRef;
    private ArrayList<String> workHistoryList;
    private ArrayAdapter<String> adapter;

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
                                        Toast.makeText(HistoryWorkActivity.this, "Η πρόσβαση σε αυτή τη σελίδα επιτρέπεται μόνο σε αγρότες.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }
                    });
        }

        setContentView(R.layout.activity_historywork);

        Button backButton = findViewById(R.id.back_button);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);

        workHistoryRef = FirebaseDatabase.getInstance().getReference().child("work_history");

        workHistoryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workHistoryList);

        ListView workHistoryListView = findViewById(R.id.history_list_view);
        workHistoryListView.setAdapter(adapter);


        getWorkHistory();


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UserProfile.class);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(CalendarActivity.class);
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
    }

    private void getWorkHistory() {
        workHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workHistoryList.clear();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    for (DataSnapshot workSnapshot : dateSnapshot.getChildren()) {
                        String work = workSnapshot.getValue(String.class);
                        workHistoryList.add(date + ": " + work);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HistoryWorkActivity.this, "Αποτυχία φόρτωσης δεδομένων. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(HistoryWorkActivity.this, cls);
        startActivity(intent);
    }
}
