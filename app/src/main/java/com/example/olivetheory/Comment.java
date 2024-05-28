package com.example.olivetheory;
import com.google.firebase.Timestamp;

public class Comment {
    private String comment, userName;
    private Timestamp timestamp;

    // Default constructor (required for Firestore)
    public Comment() {
    }

    // Constructor with parameters
    public Comment(String comment, Timestamp timestamp, String userName) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.userName = userName;
    }

    // Getter and setter methods
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


