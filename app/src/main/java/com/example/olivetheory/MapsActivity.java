package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Retrieve current user information from Firestore
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            getUserInfo(userId);
        } else {
            // Handle the case where there's no logged-in user
        }

        // Initialize Places SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up the FloatingActionButton
        FloatingActionButton fabSavedLocations = findViewById(R.id.fab_saved_locations);
        fabSavedLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an activity or fragment to display saved locations
                 Intent intent = new Intent(MapsActivity.this, SavedLocationsActivity.class);
                 startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Athens and move the camera
        LatLng athens = new LatLng(37.9838, 23.7275); // Coordinates for Athens, Greece
        mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Athens, Greece"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 10));

        // Set up a click listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear(); // Clear existing markers
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Update the latitude and longitude of the current user
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                // Create a SavedLocation object
                SavedLocation savedLocation = new SavedLocation();
                savedLocation.setLatitude(latitude);
                savedLocation.setLongitude(longitude);

                // Add the saved location to the user's list of saved locations
                if (currentUser != null) {
                    currentUser.getSavedLocations().add(savedLocation);

                    // Save the updated user object to Firestore
                    db.collection("users")
                            .document(currentUser.getUserId())
                            .set(currentUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User location updated successfully");
                                    Toast.makeText(getApplicationContext(), "Location saved successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error updating user location", e);
                                    Toast.makeText(getApplicationContext(), "Failed to save location", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void getUserInfo(String userId) {
        // Retrieve user information from Firestore based on user ID
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // User exists, populate currentUser object
                            currentUser = documentSnapshot.toObject(User.class);
                            Toast.makeText(getApplicationContext(), "User data retrieved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the case where user document doesn't exist
                            Toast.makeText(getApplicationContext(), "User document doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to retrieve user information
                        Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting user data", e);
                    }
                });
    }
}
