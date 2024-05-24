package com.example.olivetheory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumActivity extends AppCompatActivity {

    private ListView mChatListView;
    private EditText mMessageEditText;
    private ImageButton mSendButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private final List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("group_messages");

        mChatListView = findViewById(R.id.group_chat_list_view);
        mMessageEditText = findViewById(R.id.group_chat_message_view);
        mSendButton = findViewById(R.id.group_chat_send_button);

        mMessageListAdapter = new MessageAdapter(this, mMessageList);
        mChatListView.setAdapter(mMessageListAdapter);

        loadMessages();

        View appbarView = getLayoutInflater().inflate(R.layout.app_bar, null);
        RelativeLayout mainAppBar = findViewById(R.id.group_main_app_bar);
        mainAppBar.addView(appbarView);

        Button backButton = appbarView.findViewById(R.id.back_button);


        backButton.setOnClickListener(view -> finish());

        mSendButton.setOnClickListener(view -> sendMessage());
    }

    private void loadMessages() {
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    mMessageList.add(message);
                    mMessageListAdapter.notifyDataSetChanged();
                    mChatListView.setSelection(mMessageListAdapter.getCount() - 1);
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
                showError("Αποτυχία φόρτωσης του μηνύματος. Παρακαλώ ελέγξτε τη σύνδεσή σας.");
            }
        });
    }

    private void sendMessage() {
        String messageText = mMessageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            mMessageEditText.setError("Κενό μήνυμα");
            return;
        }

        DatabaseReference newMessageRef = mDatabaseReference.push();

        String pushId = newMessageRef.getKey();

        Message message = new Message(pushId, mAuth.getCurrentUser().getUid(), messageText, System.currentTimeMillis());

        newMessageRef.setValue(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mMessageEditText.setText("");
                    } else {
                        showError("Αποτυχία αποστολής μηνύματος. Παρακαλώ προσπαθήστε ξανά.");
                    }
                });
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
