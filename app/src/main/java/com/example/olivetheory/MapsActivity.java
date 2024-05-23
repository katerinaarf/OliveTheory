package com.example.olivetheory;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private User currentUser;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Initialize Places SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Retrieve current user information from Firestore
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            getUserInfo(userId);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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

        // Set up the search bar
        setUpSearchBar();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpSearchBar() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Move the camera to the selected place
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15f));

                // Add a marker at the selected place
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle errors
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        // Intercept touch events on the search input EditText to retain focus
        View searchInput = autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);
        if (searchInput instanceof EditText) {
            EditText searchEditText = (EditText) searchInput;
            searchEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Retain focus on the search input EditText
                    v.requestFocus();
                    return false;
                }
            });
        }
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
                                    Toast.makeText(getApplicationContext(), "Η τοποθεσία αποθηκεύτηκε επιτυχώς!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Αποτυχία αποθήκευσης τοποθεσίας", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Επιτυχής ενημέρωση στοιχείων χρήστη", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the case where user document doesn't exist
                            Toast.makeText(getApplicationContext(), "Παρουσιάστηκε σφάλμα κατά την ενημέρωση στοιχείων χρήστη", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to retrieve user information
                        Toast.makeText(getApplicationContext(), "Παρουσιάστηκε σφάλμα κατά την ενημέρωση στοιχείων χρήστη", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
