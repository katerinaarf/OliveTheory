package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForumListActivity extends AppCompatActivity {

    private ListView mForumListView;
    private ForumListAdapter mForumListAdapter;
    private List<ForumListItem> mForumListItems;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_place);

        mForumListView = findViewById(R.id.forum_list_view);
        mForumListItems = new ArrayList<>();
        mForumListAdapter = new ForumListAdapter(this, mForumListItems);
        mForumListView.setAdapter(mForumListAdapter);

        Button add = findViewById(R.id.add);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton logoutButton = findViewById(R.id.logout);
        Button messageButton = findViewById(R.id.message);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        messageButton.setOnClickListener(v -> startNewActivity(ChatListActivity.class));
        add.setOnClickListener(v -> showAddDialog());

        mForumListView.setOnItemClickListener((parent, view, position, id) -> {
            ForumListItem item = mForumListItems.get(position);
            Intent forumIntent = new Intent(ForumListActivity.this, ForumActivity.class);
            forumIntent.putExtra("post_id", item.getUserId());
            startActivity(forumIntent);
        });

        loadForumList();
    }

    private void loadForumList() {
        mDatabaseReference.child("forumitems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mForumListItems.clear();
                if (snapshot.getChildrenCount() == 0) {
                    showStartNewForumPost();
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.getKey();
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String userImage = dataSnapshot.child("userImage").getValue(String.class);
                        String post = dataSnapshot.child("post").getValue(String.class);
                        String time = dataSnapshot.child("time").getValue(String.class);
                        String date = dataSnapshot.child("date").getValue(String.class);
                        String dislikes = dataSnapshot.child("dislikes").getValue(String.class);

                        if (name != null && userImage != null && post != null && time != null && date != null && dislikes != null) {
                            mForumListItems.add(new ForumListItem(userId, name, userImage, post, time, date, dislikes));
                        }
                    }
                    mForumListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForumListActivity.this, "Αποτυχία φόρτωσης δημοσιεύσεων. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStartNewForumPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Δεν έχετε καμία δημοσίευση");
        builder.setMessage("Θα θέλατε να ξεκινήσετε;");
        builder.setPositiveButton("Ναι", (dialog, which) -> showAddDialog());
        builder.setNegativeButton("Όχι", null);
        builder.show();
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_post, null);
        dialogBuilder.setView(dialogView);

        EditText postText = dialogView.findViewById(R.id.add_post_txt);
        Button add = dialogView.findViewById(R.id.add);

        AlertDialog alertDialog = dialogBuilder.create();

        add.setOnClickListener(v -> {
            String postContent = postText.getText().toString().trim();
            if (!postContent.isEmpty()) {
                addNewPost(postContent);
                alertDialog.dismiss();
            } else {
                postText.setError("Παρακαλώ εισάγετε κείμενο για τη δημοσίευση");
            }
        });

        alertDialog.show();
    }

    private void addNewPost(String postContent) {
        DatabaseReference newPostRef = mDatabaseReference.child("forumitems").push();
        String postId = newPostRef.getKey();
        if (postId != null) {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Assuming you have stored the user's name and image URL in the database and you can retrieve them
            mDatabaseReference.child("users").child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName = snapshot.child("name").getValue(String.class);
                    String userImageUrl = snapshot.child("userImage").getValue(String.class);

                    if (userName != null && userImageUrl != null) {
                        ForumListItem newPost = new ForumListItem(mCurrentUserId, userName, userImageUrl, postContent, currentTime, currentDate, "0");
                        newPostRef.setValue(newPost)
                                .addOnSuccessListener(aVoid -> Toast.makeText(ForumListActivity.this, "Η δημοσίευση προστέθηκε επιτυχώς", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(ForumListActivity.this, "Αποτυχία προσθήκης δημοσίευσης. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ForumListActivity.this, "Αποτυχία λήψης στοιχείων χρήστη. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(ForumListActivity.this, cls);
        startActivity(intent);
    }
}
