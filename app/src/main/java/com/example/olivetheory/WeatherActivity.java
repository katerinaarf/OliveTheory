package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WeatherActivity extends AppCompatActivity {

    private EditText searchEditText;
    private TextView locationTextView, temperatureTextView, humidityTextView, savedLocationsTextView, searchButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String openWeatherMapApiKey, geocodingApiKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        geocodingApiKey = getString(R.string.geocoding_api_key);
        openWeatherMapApiKey = getString(R.string.openWeather_map_key);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        locationTextView = findViewById(R.id.locationTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        savedLocationsTextView = findViewById(R.id.savedloc);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = searchEditText.getText().toString().trim();
                if (!location.isEmpty()) {
                    searchLocation(location);
                } else {
                    Toast.makeText(WeatherActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        savedLocationsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSavedLocationsActivity();
            }
        });
    }

    private void searchLocation(String location) {
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + geocodingApiKey;
        Log.d("WeatherActivity", "API URL: " + apiUrl);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(apiUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        Log.e("WeatherActivity", "Error: HTTP response code " + responseCode);
                        runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "Failed to retrieve location data", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    InputStream inputStream = urlConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("WeatherActivity", "Response: " + stringBuilder.toString());
                    final String response = stringBuilder.toString();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray resultsArray = jsonObject.getJSONArray("results");

                            if (resultsArray.length() > 0) {
                                JSONObject locationObject = resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                double latitude = locationObject.getDouble("lat");
                                double longitude = locationObject.getDouble("lng");

                                locationTextView.setText("Γεωγραφικό πλάτος: " + latitude + ", Γεωγραφικό μήκος: " + longitude);
                                getWeatherData(latitude, longitude);
                            } else {
                                // Handle case where no results are returned
                                Log.e("WeatherActivity", "No results found for the provided location");
                                Toast.makeText(WeatherActivity.this, "No results found for the provided location", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("WeatherActivity", "Error parsing JSON", e);
                            Toast.makeText(WeatherActivity.this, "Error parsing location data", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    Log.e("WeatherActivity", "Network error", e);
                    runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "Failed to retrieve location data", Toast.LENGTH_SHORT).show());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Log.e("WeatherActivity", "Error closing stream", e);
                        }
                    }
                }
            }
        });
    }

    private void openSavedLocationsActivity() {
        Intent intent = new Intent(this, WeatherSavedLocations.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                locationTextView.setText("Γεωγραφικό πλάτος: " + latitude + ", Γεωραφικό μήκος: " + longitude);
                getWeatherData(latitude, longitude);
            }
        }
    }

    private void getWeatherData(double latitude, double longitude) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + openWeatherMapApiKey;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(apiUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        Log.e("WeatherActivity", "Error: HTTP response code " + responseCode);
                        return null;
                    }

                    InputStream inputStream = urlConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } catch (IOException e) {
                    Log.e("WeatherActivity", "Network error", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Log.e("WeatherActivity", "Error closing stream", e);
                        }
                    }
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = future.get();
                    if (response != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject mainObject = jsonObject.getJSONObject("main");
                                    double temperatureKelvin = mainObject.getDouble("temp");
                                    double temperatureCelsius = temperatureKelvin - 273.15; // Conversion to Celsius
                                    double humidity = mainObject.getDouble("humidity");

                                    temperatureTextView.setText("Θερμοκρασία: " + temperatureCelsius + "°C"); // Display in Celsius
                                    humidityTextView.setText("Υγρασία: " + humidity + "%");
                                } catch (JSONException e) {
                                    Log.e("WeatherActivity", "Error parsing JSON", e);
                                    Toast.makeText(WeatherActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WeatherActivity.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("WeatherActivity", "Error getting future result", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}
