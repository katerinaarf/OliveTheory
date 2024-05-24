package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private ListView mChatListView;
    private ChatListAdapter mChatListAdapter;
    private List<ChatListItem> mChatListItems;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_place);

        mChatListView = findViewById(R.id.chat_list_view);
        mChatListItems = new ArrayList<>();
        mChatListAdapter = new ChatListAdapter(this, mChatListItems);
        mChatListView.setAdapter(mChatListAdapter);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        loadChatList();

        mChatListView.setOnItemClickListener((parent, view, position, id) -> {
            ChatListItem item = mChatListItems.get(position);
            Intent chatIntent = new Intent(ChatListActivity.this, MessageActivity.class);
            chatIntent.putExtra("user_id", item.getUserId());
            startActivity(chatIntent);
        });
    }

    private void loadChatList() {
        mDatabaseReference.child("messages").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatListItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String chatUserId = dataSnapshot.getKey();
                    loadLastMessage(chatUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, "Αποτυχία φόρτωσης συνομιλιών. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLastMessage(String chatUserId) {
        mDatabaseReference.child("messages").child(mCurrentUserId).child(chatUserId).limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                                String lastMessage = messageSnapshot.child("content").getValue(String.class);
                                mChatListItems.add(new ChatListItem(chatUserId, "Username", lastMessage));
                                mChatListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatListActivity.this, "Αποτυχία φόρτωσης τελευταίου μηνύματος. ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
