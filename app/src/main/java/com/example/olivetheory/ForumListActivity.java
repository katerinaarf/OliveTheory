package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForumListActivity extends AppCompatActivity {

    private static final String TAG = "ForumListActivity";

    private ListView mForumListView;
    private ForumListAdapter mForumListAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_place);

        initializeViews();
        setupListeners();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadForumList();
    }

    private void initializeViews() {
        mForumListView = findViewById(R.id.forum_list_view);
        mForumListAdapter = new ForumListAdapter(this, new ArrayList<>());
        mForumListView.setAdapter(mForumListAdapter);
    }

    private void setupListeners() {
        ImageButton user = findViewById(R.id.user);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton weatherButton = findViewById(R.id.weather);
        ImageButton messageButton = findViewById(R.id.message);
        ImageButton problems = findViewById(R.id.problems);
        ImageButton add = findViewById(R.id.add);

        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        messageButton.setOnClickListener(v -> startNewActivity(ChatListActivity.class));
        problems.setOnClickListener(v -> startNewActivity(ProblemsActivity.class));
        add.setOnClickListener(v -> showAddDialog());
    }

    private void loadForumList() {
        db.collection("forumitems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ForumListItem> forumListItems = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        ForumListItem item = document.toObject(ForumListItem.class);
                        if (item != null) {
                            item.setPostId(document.getId()); // Set postId from document ID
                            forumListItems.add(item);
                        }
                    }
                    Log.d(TAG, "Number of items retrieved: " + forumListItems.size());
                    mForumListAdapter.setData(forumListItems);
                    Log.d(TAG, "Forum list loaded successfully");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ForumListActivity.this, "Failed to load forum posts", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting documents", e);
                });
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
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
                postText.setError("Please enter text for the post");
            }
        });

        alertDialog.show();
    }

    private void addNewPost(String postContent) {
        DocumentReference userRef = db.collection("users").document(mCurrentUserId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    String userName = user.getName();
                    Log.d(TAG, "User name retrieved: " + userName);

                    // Fallback to userId if name is not available
                    if (userName == null || userName.isEmpty()) {
                        userName = mCurrentUserId;
                    }

                    Map<String, Object> post = new HashMap<>();
                    post.put("userId", mCurrentUserId);
                    post.put("name", userName);
                    post.put("post", postContent);
                    post.put("time", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    post.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                    post.put("likes", 0L);

                    db.collection("forumitems")
                            .add(post)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(ForumListActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "New post added successfully with ID: " + documentReference.getId());
                                loadForumList();  // Reload the forum list
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ForumListActivity.this, "Failed to add post. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error adding document", e);
                            });
                } else {
                    Toast.makeText(ForumListActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User object is null");
                }
            } else {
                Toast.makeText(ForumListActivity.this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User document does not exist");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ForumListActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error retrieving user document", e);
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(ForumListActivity.this, cls);
        startActivity(intent);
    }
}
