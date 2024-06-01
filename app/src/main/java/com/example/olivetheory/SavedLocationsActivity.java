package com.example.olivetheory;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {

    private static final String TAG = "SavedLocationsActivity";

    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        getSavedLocationsFromFirestore();
    }

    // Method to retrieve saved locations from Firestore
    private void getSavedLocationsFromFirestore() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null && currentUser.getSavedLocations() != null) {
                            updateListView(currentUser.getSavedLocations());
                        } else {
                            Log.d(TAG, "User has no saved locations");
                            Toast.makeText(SavedLocationsActivity.this, "No saved locations found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "User document does not exist");
                        Toast.makeText(SavedLocationsActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error retrieving user document", e);
                    // Inform the user about the error
                    Toast.makeText(SavedLocationsActivity.this, "Failed to retrieve user document", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(SavedLocationsActivity.this, "No authenticated user", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the ListView with saved locations
    private void updateListView(List<SavedLocation> savedLocations) {
        ListView savedLocationsListView = findViewById(R.id.savedLocationsListView);

        ArrayAdapter<SavedLocation> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, savedLocations);

        savedLocationsListView.setAdapter(adapter);
    }
}
