package com.example.olivetheory;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 200;

    private ListView mChatListView;
    private EditText mMessageEditText;
    private ImageButton mSendButton, addButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

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
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        mChatListView = findViewById(R.id.chat_list_view);
        mMessageEditText = findViewById(R.id.chatMessageView);
        mSendButton = findViewById(R.id.chatSendButton);
        addButton = findViewById(R.id.chatAddButton);

        mMessageListAdapter = new MessageAdapter(this, mMessageList, mCurrentUserId);
        mChatListView.setAdapter(mMessageListAdapter);

        loadMessages();

        View appbarView = LayoutInflater.from(this).inflate(R.layout.app_bar, null);
        RelativeLayout mainAppBar = findViewById(R.id.main_app_bar);
        mainAppBar.addView(appbarView);

        ImageView user = appbarView.findViewById(R.id.userpro);
        Button backButton = appbarView.findViewById(R.id.back_button);

        backButton.setOnClickListener(view -> finish());
        mSendButton.setOnClickListener(view -> sendMessage());

        user.setOnClickListener(v -> {
            Intent userIntent = new Intent(MessageActivity.this, UserProfile.class);
            startActivity(userIntent);
        });

        addButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        StorageReference fileRef = mStorageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    sendMessageWithImage(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void sendMessageWithImage(String imageUrl) {
        DatabaseReference current_user_ref = mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId).push();
        DatabaseReference chat_user_ref = mDatabaseReference.child("messages").child(mChatUserId).child(mCurrentUserId).push();

        String pushId = current_user_ref.getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("content", "Image");
        messageMap.put("senderId", mCurrentUserId);
        messageMap.put("recipientId", mChatUserId);
        messageMap.put("imageUrl", imageUrl);
        messageMap.put("timestamp", System.currentTimeMillis());

        Map<String, Object> messageUserMap = new HashMap<>();
        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUserId + "/" + pushId, messageMap);
        messageUserMap.put("messages/" + mChatUserId + "/" + mCurrentUserId + "/" + pushId, messageMap);

        mDatabaseReference.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                showError("Αποτυχία αποστολής. Προσπαθήστε ξανά.");
            }
        });
    }

    private void loadMessages() {
        DatabaseReference chatRef = mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId);
        chatRef.addChildEventListener(new ChildEventListener() {
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
