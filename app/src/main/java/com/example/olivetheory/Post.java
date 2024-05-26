package com.example.olivetheory;

public class Post {

    private String userId;
    private String name;
    private String date;
    private String time;
    private String userImage;
    private String answer;

    public Post(String pushId, String uid, String forumText, long l){}

    public Post(String userId, String name, String date, String time, String userImage, String answer){
        this.userId = userId;
        this.name = name;
        this.date = date;
        this.time = time;
        this.userImage = userImage;
        this.answer = answer;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}



