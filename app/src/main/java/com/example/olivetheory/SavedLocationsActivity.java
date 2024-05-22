package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        // Assuming you have a ListView in your layout file with the id "savedLocationsListView"
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView savedLocationsListView = findViewById(R.id.savedLocationsListView);

        // Get the list of saved locations from the intent or from a ViewModel or any other way you manage data
        List<SavedLocation> savedLocations = new ArrayList<>(); // Get the actual list of saved locations

        // Create an ArrayAdapter to display the saved locations in the ListView
        ArrayAdapter<SavedLocation> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, savedLocations);

        // Set the adapter to the ListView
        savedLocationsListView.setAdapter(adapter);
    }
}

