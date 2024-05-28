package com.example.olivetheory;

import android.annotation.SuppressLint;
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
    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        workRef = FirebaseDatabase.getInstance().getReference().child("work_history");
        suggestRef = FirebaseDatabase.getInstance().getReference().child("suggestions");


        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                showAddWorkDialog(selectedDate);
            }
        });

        // Buttons
        Button calendar = findViewById(R.id.calendar_second);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton problemsButton = findViewById(R.id.problems);
        Button messageButton = findViewById(R.id.message);
        Button forumButton = findViewById(R.id.forum);

        Button suggestButton = findViewById(R.id.suggest);

        Button historyButton = findViewById(R.id.history_work);

        // Change Activity
        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        calendar.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        problemsButton.setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        messageButton.setOnClickListener(v -> startNewActivity(ChatListActivity.class));
        forumButton.setOnClickListener(v -> startNewActivity(ForumListActivity.class));
        historyButton.setOnClickListener(v -> startNewActivity(HistoryWorkActivity.class));

        suggestButton.setOnClickListener(v -> suggestWork());
    }

    private void showAddWorkDialog(String date) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_work, null);
        dialogBuilder.setView(dialogView);

        EditText addwork = dialogView.findViewById(R.id.add_work_txt);
        Button add = dialogView.findViewById(R.id.add);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button cancel = dialogView.findViewById(R.id.cancel);
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
        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestWorkList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Προτεινόμενες Εργασίες");

        View viewInflated = getLayoutInflater().inflate(R.layout.activity_suggest, null);
        final ListView suggest = viewInflated.findViewById(R.id.suggestText);
        suggest.setAdapter(suggestAdapter);

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