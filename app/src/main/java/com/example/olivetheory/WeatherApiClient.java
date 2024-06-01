package com.example.olivetheory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherApiClient {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "90ff947bb8a861cf1b5094e4d5fc12c5";
    private static WeatherService weatherService;

    public static WeatherService getWeatherService() {
        if (weatherService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weatherService = retrofit.create(WeatherService.class);
        }
        return weatherService;
    }

    public interface WeatherService {
        @GET("weather")
        Call<WeatherResponse> getWeatherData(@Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String apiKey, @Query("units") String units);
    }
}