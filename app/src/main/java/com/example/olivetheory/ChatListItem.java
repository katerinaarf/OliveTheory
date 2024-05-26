package com.example.olivetheory;

public class ChatListItem {
    private String userId;
    private String name;
    private String lastMessage;

    public ChatListItem(String userId, String name, String lastMessage) {
        this.userId = userId;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
