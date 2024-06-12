package com.example.olivetheory.models;

public class ChatListItem {
    private String userId, name, lastMessage, usertype;

    public ChatListItem(String userId, String name, String lastMessage, String usertype) {
        this.userId = userId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.usertype = usertype;
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

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
