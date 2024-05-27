package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private DatabaseReference workRef;
    private DatabaseReference suggestRef;
    private ArrayList<String> suggestWorkList;
    private ArrayAdapter<String> suggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Firebase references
        workRef = FirebaseDatabase.getInstance().getReference().child("work_history");
        suggestRef = FirebaseDatabase.getInstance().getReference().child("suggestions");

        // Buttons
        Button calendar = findViewById(R.id.calendar_second);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);

        TextView suggestButton = findViewById(R.id.suggest);
        TextView addButton = findViewById(R.id.add);
        TextView historyButton = findViewById(R.id.history_work);

        // Change Activity
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UserProfile.class);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
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

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(HistoryWorkActivity.class);
            }
        });

        suggestButton.setOnClickListener(v -> suggestWork());
        addButton.setOnClickListener(v -> addWork());
    }

    private void addWork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Προσθήκη Εργασίας");

        View viewInflated = getLayoutInflater().inflate(R.layout.add_work, null);
        final EditText addwork = viewInflated.findViewById(R.id.add_work_txt);
        Button send = viewInflated.findViewById(R.id.add);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String work = addwork.getText().toString();
            if (!work.isEmpty()) {
                workRef.push().setValue(work);
                Toast.makeText(CalendarActivity.this, "Εργασία προστέθηκε", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CalendarActivity.this, "Το πεδίο δεν μπορεί να είναι κενό", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void suggestWork() {
        suggestWorkList = new ArrayList<>();
        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestWorkList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Προτεινόμενες Εργασίες");

        View viewInflated = getLayoutInflater().inflate(R.layout.activity_suggest, null);
        final ListView suggest = viewInflated.findViewById(R.id.suggestText);
        Button close = viewInflated.findViewById(R.id.close);
        suggest.setAdapter(suggestAdapter);

        builder.setView(viewInflated);

        suggestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestWorkList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String suggestion = snapshot.getValue(String.class);
                    suggestWorkList.add(suggestion);
                }
                suggestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "Αποτυχία φόρτωσης δεδομένων. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(CalendarActivity.this, cls);
        startActivity(intent);
    }
}
