package com.example.olivetheory.gui.mainactivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olivetheory.R;
import com.example.olivetheory.adapter.ChatListAdapter;
import com.example.olivetheory.gui.usermanagment.UserProfile;
import com.example.olivetheory.models.ChatListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private ListView mChatListView;
    private ChatListAdapter mChatListAdapter;
    private List<ChatListItem> mChatListItems;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mChatListView = findViewById(R.id.chat_list_view);
        ImageButton addChatButton = findViewById(R.id.add_chat_button);
        mChatListItems = new ArrayList<>();
        mChatListAdapter = new ChatListAdapter(this, mChatListItems);
        mChatListView.setAdapter(mChatListAdapter);
        mFirestore = FirebaseFirestore.getInstance();

        ImageButton user = findViewById(R.id.user);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton weatherButton = findViewById(R.id.weather);
        ImageButton logoutButton = findViewById(R.id.problems);
        ImageButton forumButton = findViewById(R.id.forum);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if the user has existing chats
        loadChatList();

        mChatListView.setOnItemClickListener((parent, view, position, id) -> {
            ChatListItem clickedItem = mChatListItems.get(position);
            String chatUserId = clickedItem.getUserId();

            Intent messageIntent = new Intent(ChatListActivity.this, MessageActivity.class);
            messageIntent.putExtra("user_id", chatUserId);
            startActivity(messageIntent);
        });

        addChatButton.setOnClickListener(v -> showSearchDialog());
        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));
        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));
        forumButton.setOnClickListener(v -> startNewActivity(ForumListActivity.class));
    }

    // Method to load the user's existing chats
    private void loadChatList() {
        mDatabaseReference.child("messages").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatListItems.clear();
                if (snapshot.getChildrenCount() == 0) {
                    findViewById(R.id.add_chat_button).setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String chatUserId = dataSnapshot.getKey();
                        if (chatUserId != null) {
                            loadLastMessage(chatUserId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, "Failed to load chats. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showSearchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_search_user, null);
        dialogBuilder.setView(dialogView);

        EditText userIdEditText = dialogView.findViewById(R.id.user_id_edit_text);
        TextView searchButton = dialogView.findViewById(R.id.search_button);

        AlertDialog alertDialog = dialogBuilder.create();

        searchButton.setOnClickListener(v -> {
            String userId = userIdEditText.getText().toString().trim();
            if (!userId.isEmpty()) {
                searchUser(userId, alertDialog);
            } else {
                userIdEditText.setError("Παρακαλώ εισάγετε το όνομα χρήστη");
            }
        });

        alertDialog.show();
    }

    private void searchUser(String name, AlertDialog alertDialog) {
        mFirestore.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userId = documentSnapshot.getId();
                            String userName = documentSnapshot.getString("name");
                            String userType = documentSnapshot.getString("type");
                            showUserFoundDialog(userId, userName, alertDialog);
                            return;
                        }
                    } else {
                        Toast.makeText(ChatListActivity.this, "Ο χρήστης δεν βρέθηκε. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatListActivity.this, "Αποτυχία αναζήτησης χρήστη. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showUserFoundDialog(String userId, String name, AlertDialog alertDialog) {
        alertDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Θέλετε να ξεκινήσετε συνομιλία με τον χρήστη:");
        builder.setMessage(name);
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent chatIntent = new Intent(ChatListActivity.this, MessageActivity.class);
                chatIntent.putExtra("user_id", userId);
                chatIntent.putExtra("user_name", name); // Pass the user name to MessageActivity
                startActivity(chatIntent);
            }
        });
        builder.setNegativeButton("Άκυρο", null);
        builder.show();
    }

    private void loadLastMessage(String chatUserId) {
        mFirestore.collection("users").document(chatUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        String userType = documentSnapshot.getString("userType");
                        if (userName != null) {
                            mDatabaseReference.child("messages").child(mCurrentUserId).child(chatUserId).limitToLast(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                                                    String lastMessage = messageSnapshot.child("content").getValue(String.class);
                                                    Log.d("ChatListActivity", "Last message: " + lastMessage);
                                                    mChatListItems.add(new ChatListItem(chatUserId, userName, lastMessage, userType));
                                                    mChatListAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatListActivity.this, "Failed to load last message.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d("ChatListActivity", "User name not found for user: " + chatUserId);
                        }
                    } else {
                        Log.d("ChatListActivity", "User not found: " + chatUserId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatListActivity.this, "Failed to load user name.", Toast.LENGTH_SHORT).show();
                });
    }


    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(ChatListActivity.this, cls);
        startActivity(intent);
    }
}
