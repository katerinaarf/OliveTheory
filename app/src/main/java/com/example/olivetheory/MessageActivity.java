package com.example.olivetheory;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class MessageActivity extends AppCompatActivity {

    private ListView mChatListView;
    private EditText mMessageEditText;
    private ImageButton mSendButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private String mCurrentUserId;
    private String mChatUserId;

    private final List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUserId = getIntent().getStringExtra("user_id");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mChatListView = findViewById(R.id.chat_list_view);
        mMessageEditText = findViewById(R.id.chatMessageView);
        mSendButton = findViewById(R.id.chatSendButton);

        mMessageListAdapter = new MessageAdapter(this, mMessageList);
        mChatListView.setAdapter(mMessageListAdapter);

        loadMessages();

        mSendButton.setOnClickListener(view -> sendMessage());
    }

    private void loadMessages() {
        mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId)
                .addChildEventListener(new ChildEventListener() {
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
                        showError("Αποτυχία φόρτωσης του μηνύματος . Παρακαλώ ελέγξτε την σύνδεσή σας.");
                    }
                });
    }

    private void sendMessage() {
        String messageText = mMessageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            mMessageEditText.setError("Κενό μήνυμα");
            return;
        }

        DatabaseReference current_user_ref = mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId).push();
        DatabaseReference chat_user_ref = mDatabaseReference.child("messages").child(mChatUserId).child(mCurrentUserId).push();

        String pushId = current_user_ref.getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("content", messageText);
        messageMap.put("senderId", mCurrentUserId);
        messageMap.put("recipientId", mChatUserId);
        messageMap.put("timestamp", System.currentTimeMillis());

        Map<String, Object> messageUserMap = new HashMap<>();
        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUserId + "/" + pushId, messageMap);
        messageUserMap.put("messages/" + mChatUserId + "/" + mCurrentUserId + "/" + pushId, messageMap);

        mDatabaseReference.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                showError("Αποτυχία αποστολής. Προσπαθήστε ξανά.");
            } else {
                mMessageEditText.setText("");
            }
        });
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
