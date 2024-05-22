package com.example.olivetheory;

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
        ListView savedLocationsListView = findViewById(R.id.savedLocationsListView);

        // Get the list of saved locations from the intent or from a ViewModel or any other way you manage data
        List<SavedLocation> savedLocations = getSavedLocations(); // Get the actual list of saved locations

        // Create an ArrayAdapter to display the saved locations in the ListView
        ArrayAdapter<SavedLocation> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, savedLocations);

        // Set the adapter to the ListView
        savedLocationsListView.setAdapter(adapter);
    }

    // Mock method to get saved locations, replace with actual data retrieval logic
    private List<SavedLocation> getSavedLocations() {
        List<SavedLocation> savedLocations = new ArrayList<>();
        // Add some mock data
        savedLocations.add(new SavedLocation(37.9838, 23.7275)); // Athens, Greece
        savedLocations.add(new SavedLocation(40.7128, -74.0060)); // New York, USA
        return savedLocations;
    }
}
