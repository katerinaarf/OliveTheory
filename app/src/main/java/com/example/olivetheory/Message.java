package com.example.olivetheory;

public class Message {
    private String content;
    private String senderId;
    private String recipientId;
    private long timestamp;
    private String imageUrl;  // Προσθήκη του URL της εικόνας

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String content, String senderId, String recipientId, long timestamp) {
        this.content = content;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.timestamp = timestamp;
    }

    public Message(String content, String senderId, String recipientId, long timestamp, String imageUrl) {
        this.content = content;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
