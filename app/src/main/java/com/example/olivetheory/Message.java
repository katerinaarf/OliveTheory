package com.example.olivetheory;


import java.util.Date;

public class Message {
    private String content, senderId, recipientId;
    private long timestamp;

    public Message() {

    }

    public Message(String content, String senderId, String recipientId) {
        this.content = content;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public Message(String pushId, String uid, String messageText, long l) {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}