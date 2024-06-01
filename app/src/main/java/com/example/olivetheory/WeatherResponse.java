package com.example.olivetheory;

public class WeatherResponse {
    private Main main;
    private String name;

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public class Main {
        private double temp;
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }
}
