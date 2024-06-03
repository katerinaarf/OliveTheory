package com.example.olivetheory;

public class ForumListItem {
    private String userId, name, userImage, post, time, date, postId, userType;
    private Long likes;

    public ForumListItem() {
    }

    public ForumListItem(String userId, String name,String userType, String userImage, String post, String time, String date, Long likes) {
        this.userId = userId;
        this.name = name;
        this.userImage = userImage;
        this.post = post;
        this.time = time;
        this.date = date;
        this.likes = likes;
    }

    // Getters and setters
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

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

}
