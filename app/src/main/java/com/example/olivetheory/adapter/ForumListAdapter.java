package com.example.olivetheory.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olivetheory.models.Comment;
import com.example.olivetheory.models.ForumListItem;
import com.example.olivetheory.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumListAdapter extends ArrayAdapter<ForumListItem> {
    private final Activity context;
    private List<ForumListItem> forumListItems;
    private FirebaseFirestore db;

    public ForumListAdapter(Activity context, List<ForumListItem> forumListItems) {
        super(context, R.layout.item_furom_list, forumListItems);
        this.context = context;
        this.forumListItems = forumListItems;
        db = FirebaseFirestore.getInstance();
    }

    public void setData(List<ForumListItem> forumListItems) {
        db.collection("forumitems").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.forumListItems.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    ForumListItem item = document.toObject(ForumListItem.class);
                    if (item != null) {
                        item.setPostId(document.getId());
                        Log.d(TAG, "Post fetched: " + item.getPost());
                        this.forumListItems.add(item);
                    }
                }
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        TextView name, userType, post, date, time, likes;
        ImageButton likeButton, commentButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_furom_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.post = convertView.findViewById(R.id.post);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.likes = convertView.findViewById(R.id.likes);
            viewHolder.likeButton = convertView.findViewById(R.id.likeButton);
            viewHolder.commentButton = convertView.findViewById(R.id.commentButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForumListItem item = forumListItems.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.post.setText(item.getPost());
        viewHolder.date.setText(item.getDate());
        viewHolder.time.setText(item.getTime());
        viewHolder.likes.setText(item.getLikes() + " Likes");

        // Like button listener
        viewHolder.likeButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                checkAndLikePost(item, userId, viewHolder.likes);
            } else {
                Toast.makeText(context, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            }
        });

        // Comment button listener
        viewHolder.commentButton.setOnClickListener(v -> showCommentsDialog(item));

        return convertView;
    }

    private void checkAndLikePost(ForumListItem item, String userId, TextView likesTextView) {
        String postId = item.getPostId();
        if (postId != null) {
            db.collection("forumitems").document(postId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> likedUsers = (List<String>) documentSnapshot.get("likedUsers");
                            if (likedUsers == null) {
                                likedUsers = new ArrayList<>();
                            }
                            if (likedUsers.contains(userId)) {
                                Toast.makeText(context, "You have already liked this post", Toast.LENGTH_SHORT).show();
                            } else {
                                // Increment likes locally
                                item.setLikes(item.getLikes() + 1);
                                // Update TextView
                                likesTextView.setText(item.getLikes() + " Likes");
                                // Update Firestore
                                updateLikesInDatabase(item, userId);
                            }
                        } else {
                            Toast.makeText(context, "Post not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to check likes. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error checking likes", e);
                    });
        } else {
            Toast.makeText(context, "Post ID is null. Unable to like post.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLikesInDatabase(ForumListItem item, String userId) {
        String postId = item.getPostId();
        Map<String, Object> updates = new HashMap<>();
        updates.put("likes", item.getLikes());
        updates.put("likedUsers", FieldValue.arrayUnion(userId)); // Add userId to likedUsers array

        db.collection("forumitems").document(postId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Like updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update like. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating likes", e);
                });
    }

    private void showCommentsDialog(ForumListItem item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        TextView addCommentButton = dialogView.findViewById(R.id.addCommentButton);

        // Set up RecyclerView with comments adapter and fetch comments
        CommentsAdapter adapter = new CommentsAdapter(context, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        fetchCommentsFromFirestore(item, adapter);

        addCommentButton.setOnClickListener(v -> {
            showAddCommentDialog(item);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Comments")
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchCommentsFromFirestore(ForumListItem item, CommentsAdapter adapter) {
        String postId = item.getPostId();
        if (postId != null) {
            db.collection("forumitems").document(postId).collection("comments")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Comment> comments = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Comment comment = document.toObject(Comment.class);
                            comments.add(comment);
                        }
                        adapter.setData(comments);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to fetch comments. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching comments", e);
                    });
        } else {
            Toast.makeText(context, "Post ID is null. Unable to fetch comments.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddCommentDialog(ForumListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Comment");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Fetch the current user's UID
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    // Fetch the username associated with the UID
                    fetchUserName(userId, commentText, item);
                } else {
                    Toast.makeText(context, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchUserName(String userId, String commentText, ForumListItem item) {
        // Fetch the user document from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the username and user type from the document
                        String userName = documentSnapshot.getString("name");
                        String userType = documentSnapshot.getString("userType");
                        if (userName != null && userType != null) {
                            // Create the comment with the username and user type
                            Comment newComment = new Comment(commentText, new Timestamp(new Date()), userName);
                            // Add the comment to Firestore
                            addCommentToFirestore(item, newComment);
                        } else {
                            Toast.makeText(context, "User details not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user details", e);
                });
    }

    private void addCommentToFirestore(ForumListItem item, Comment comment) {
        db.collection("forumitems").document(item.getPostId()).collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Comment added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add comment. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding comment", e);
                });
    }

}
