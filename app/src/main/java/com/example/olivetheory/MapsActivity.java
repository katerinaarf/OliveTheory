package com.example.olivetheory;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private SearchView searchView;
    private FirebaseFirestore db;
    private FloatingActionButton fabSavedLocations;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean isUserInteracting = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();

        fabSavedLocations = findViewById(R.id.fab_saved_locations);
        fabSavedLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an activity or fragment to display saved locations
                Intent intent = new Intent(MapsActivity.this, SavedLocationsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        placesClient = Places.createClient(this);

        // Initialize search view
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the search
                searchPlaces(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        Location location = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mMap != null && !isUserInteracting) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                        }
                    }
                }
            }, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationUpdates();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isUserInteracting = true;
                showLocationNameDialog(latLng);
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    isUserInteracting = true;
                }
            }
        });
    }


    private void showLocationNameDialog(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter location name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationName = input.getText().toString();
                if (!locationName.isEmpty()) {
                    saveLocationToFirestore(latLng, locationName);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a name for the location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void searchPlaces(String query) {
        Log.d(TAG, "Searching for: " + query);

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            Log.d(TAG, "Predictions count: " + response.getAutocompletePredictions().size());
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.d(TAG, "Prediction: " + prediction.getPrimaryText(null));
                fetchPlaceDetails(prediction.getPlaceId());
            }
        }).addOnFailureListener((exception) -> {
            Log.e(TAG, "Prediction fetching failed: " + exception.getMessage());
        });
    }

    private void fetchPlaceDetails(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener((responseFetch) -> {
            Place place = responseFetch.getPlace();
            Log.d(TAG, "Place found: " + place.getName());
            LatLng placeLatLng = place.getLatLng();
            if (placeLatLng != null) {
                mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f));
                isUserInteracting = true;
            }
        }).addOnFailureListener((exceptionFetch) -> {
            Log.e(TAG, "Place not found: " + exceptionFetch.getMessage());
        });
    }


    private void saveLocationToFirestore(LatLng latLng, String locationName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            SavedLocation savedLocation = new SavedLocation();
            savedLocation.setName(locationName);
            savedLocation.setLatitude(latLng.latitude);
            savedLocation.setLongitude(latLng.longitude);

            String userId = firebaseUser.getUid();
            String documentPath = "users/" + userId;

            DocumentReference userRef = FirebaseFirestore.getInstance().document(documentPath);

            userRef.update("savedLocations", FieldValue.arrayUnion(savedLocation))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Location saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to save location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Places.deinitialize();
    }

}
