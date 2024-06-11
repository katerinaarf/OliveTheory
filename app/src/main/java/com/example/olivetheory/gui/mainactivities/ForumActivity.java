package com.example.olivetheory.gui.mainactivities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olivetheory.R;
import com.example.olivetheory.adapter.ForumAdapter;
import com.example.olivetheory.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private ListView mForumListView;
    private EditText mForumEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private String mPostId;

    private final List<Post> mForumList = new ArrayList<>();
    private ForumAdapter mForumListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("forum_posts");

        mForumListView = findViewById(R.id.forum_list_view);
        mForumEditText = findViewById(R.id.answer_view);
        ImageButton mPostButton = findViewById(R.id.post_button);
        ImageButton back = findViewById(R.id.back);

        mForumListAdapter = new ForumAdapter(this, mForumList);
        mForumListView.setAdapter(mForumListAdapter);

        if (getIntent().hasExtra("post_id")) {
            mPostId = getIntent().getStringExtra("post_id");
        }

        loadForum();

        back.setOnClickListener(view -> finish());

        mPostButton.setOnClickListener(view -> sendPost());
    }

    private void loadForum() {
        mDatabaseReference.child(mPostId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Post forum = dataSnapshot.getValue(Post.class);
                if (forum != null) {
                    mForumList.add(forum);
                    mForumListAdapter.notifyDataSetChanged();
                    mForumListView.setSelection(mForumListAdapter.getCount() - 1);
                } else {
                    showError("Δεν υπάρχουν δεδομένα.");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Αποτυχία φόρτωσης της δημοσίευσης. Παρακαλώ ελέγξτε τη σύνδεσή σας.");
            }
        });
    }

    private void sendPost() {
        String forumText = mForumEditText.getText().toString().trim();
        if (forumText.isEmpty()) {
            mForumEditText.setError("Κενή δημοσίευση");
            return;
        }

        DatabaseReference newForumRef = mDatabaseReference.child(mPostId).push();

        String pushId = newForumRef.getKey();

        Post forum = new Post(pushId, mAuth.getCurrentUser().getUid(), forumText, System.currentTimeMillis());

        newForumRef.setValue(forum)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mForumEditText.setText("");
                    } else {
                        showError("Αποτυχία δημοσίευσης στο forum. Παρακαλώ προσπαθήστε ξανά.");
                    }
                });
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
