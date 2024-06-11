package com.example.olivetheory.models;

public class ChatListItem {
    private String userId, name, lastMessage;

    public ChatListItem(String userId, String name, String lastMessage) {
        this.userId = userId;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
