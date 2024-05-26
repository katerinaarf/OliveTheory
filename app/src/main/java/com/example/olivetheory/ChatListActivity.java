package com.example.olivetheory;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        setContentView(R.layout.dialog_place);

        mChatListView = findViewById(R.id.chat_list_view);
        mChatListItems = new ArrayList<>();
        mChatListAdapter = new ChatListAdapter(this, mChatListItems);
        mChatListView.setAdapter(mChatListAdapter);
        mFirestore = FirebaseFirestore.getInstance();

        Button search = findViewById(R.id.search);
        Button user = findViewById(R.id.user);
        Button calendarButton = findViewById(R.id.calendar);
        Button weatherButton = findViewById(R.id.weather);
        ImageButton logoutButton = findViewById(R.id.logout);
        Button forumButton = findViewById(R.id.forum);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        loadChatList();
        showStartNewChatDialog();

        user.setOnClickListener(v -> startNewActivity(UserProfile.class));

        calendarButton.setOnClickListener(v -> startNewActivity(CalendarActivity.class));

        weatherButton.setOnClickListener(v -> startNewActivity(WeatherActivity.class));

        forumButton.setOnClickListener(v -> startNewActivity(ForumListActivity.class));

        search.setOnClickListener(v -> showSearchDialog());

        mChatListView.setOnItemClickListener((parent, view, position, id) -> {
            ChatListItem item = mChatListItems.get(position);
            Intent chatIntent = new Intent(ChatListActivity.this, MessageActivity.class);
            chatIntent.putExtra("user_id", item.getUserId());
            startActivity(chatIntent);
        });
    }

//    private void loadChatList() {
//        mDatabaseReference.child("messages").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mChatListItems.clear();
//                if (snapshot.getChildrenCount() == 0) {
//                    // User doesn't have any discussions, prompt to start a new chat
//                    showStartNewChatDialog();
//                } else {
//                    // User has discussions, load them
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String chatUserId = dataSnapshot.getKey();
//                        loadLastMessage(chatUserId);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChatListActivity.this, "Αποτυχία φόρτωσης συνομιλιών. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void showStartNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Δεν έχετε καμία συνομιλία");
        builder.setMessage("Θα θέλατε να ξεκινήσετε;");
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start a new chat, show search dialog
                showSearchDialog();
            }
        });
        builder.setNegativeButton("Όχι", null);
        builder.show();
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

    private void showSearchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_search_user, null);
        dialogBuilder.setView(dialogView);

        EditText userIdEditText = dialogView.findViewById(R.id.user_id_edit_text);
        Button searchButton = dialogView.findViewById(R.id.search_button);

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
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String userId = documentSnapshot.getId();
                                String userName = documentSnapshot.getString("name");
                                showUserFoundDialog(userId, userName, alertDialog);
                                return;
                            }
                        } else {
                            Toast.makeText(ChatListActivity.this, "Ο χρήστης δεν βρέθηκε. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatListActivity.this, "Αποτυχία αναζήτησης χρήστη. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showUserFoundDialog(String userId, String name, AlertDialog alertDialog) {
        alertDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Θέλετε να ξεκινήσετε συνομιλία με τον χρήστη:");
        builder.setMessage(name+";");
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent chatIntent = new Intent(ChatListActivity.this, MessageActivity.class);
                chatIntent.putExtra("user_id", userId);
                startActivity(chatIntent);
            }
        });
        builder.setNegativeButton("Άκυρο", null);
        builder.show();
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(ChatListActivity.this, cls);
        startActivity(intent);
    }
}