package com.example.olivetheory.models;

import java.util.List;

public class User {
    private String name;
    private String email;
    private String userType;
    private String userId;
    private List<SavedLocation> savedLocations;

    // Public no-argument constructor required for Firestore serialization
    public User() {}

    public User(String name, String email, String userType, String userId) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.userId = userId;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userType = userId;
    }
    public List<SavedLocation> getSavedLocations() {
        return savedLocations;
    }

    public void setSavedLocations(List<SavedLocation> savedLocations) {
        this.savedLocations = savedLocations;
    }


}
