package com.example.olivetheory;

public class Conversation {
    private String id, userId, lastMessage;

    public Conversation() {
    }

    public Conversation(String id, String userId, String lastMessage) {
        this.id = id;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
