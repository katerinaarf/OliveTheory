// SavedLocationsActivity.java

package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WeatherSavedLocations extends AppCompatActivity {

    private ListView savedLocationsListView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        savedLocationsListView = findViewById(R.id.savedLocationsListView);

        loadSavedLocations();
    }

    private void loadSavedLocations() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null && user.getSavedLocations() != null) {
                                List<SavedLocation> savedLocations = user.getSavedLocations();
                                ArrayAdapter<SavedLocation> adapter = new ArrayAdapter<>(this,
                                        android.R.layout.simple_list_item_1, savedLocations);
                                savedLocationsListView.setAdapter(adapter);

                                savedLocationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        SavedLocation selectedLocation = savedLocations.get(position);
                                        returnResult(selectedLocation.getLatitude(), selectedLocation.getLongitude());

                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("latitude", selectedLocation.getLatitude());
                                        resultIntent.putExtra("longitude", selectedLocation.getLongitude());
                                        setResult(RESULT_OK, resultIntent);
                                        finish();

                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void returnResult(double latitude, double longitude) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", latitude);
        resultIntent.putExtra("longitude", longitude);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}