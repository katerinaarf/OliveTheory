package com.example.olivetheory;

public class ForumListItem {

    private String userId;
    private String name;
    private String userImage;
    private String post;
    private String time;
    private String date;
    private String dislikes;

    public ForumListItem(String pushId, String uid, String forumText, long l){}

    public ForumListItem(String userId, String name, String userImage, String post, String time, String date, String dislikes) {
        this.userId = userId;
        this.name = name;
        this.userImage = userImage;
        this.post = post;
        this.time = time;
        this.date = date;
        this.dislikes = dislikes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDislikes(){ return dislikes; }

    public void setDislikes(String dislikes){this.dislikes = dislikes;}
}
