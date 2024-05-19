package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private LatLng selectedLatLng;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Retrieve the user object from the Intent
        user = (User) getIntent().getSerializableExtra("KEY_USERNAME");
        if (user == null) {
            // Handle the case where user object is not passed correctly
            Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Places SDK
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        setUpSearchBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLatLng = latLng;
        });

    }

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
                // Get the selected place
                selectedLatLng = place.getLatLng();
                if (selectedLatLng != null) {
                    // Move the marker to the selected place
                    if (marker != null) {
                        marker.setPosition(selectedLatLng);
                    } else {
                        marker = mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Selected Location"));
                    }
                    // Animate camera to the selected place
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
                }
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // Handle the error.
                Toast.makeText(MapsActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        if (selectedLatLng != null) {
            resultIntent.putExtra("latitude", selectedLatLng.latitude);
            resultIntent.putExtra("longitude", selectedLatLng.longitude);
        }
        resultIntent.putExtra("USER", user);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
