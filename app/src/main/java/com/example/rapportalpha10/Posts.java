package com.example.rapportalpha10;

public class Posts {
    public String uid;
    public String username;
    public String job;
    public String city;
    public String time;
    public String date;
    public String postimage;
    public String profileimage;
    public String description;

    public Posts()
    {

    }

    public Posts(String uid, String username, String job, String city, String time, String date, String postimage,String profileimage, String description) {
        this.uid = uid;
        this.username = username;
        this.job = job;
        this.city = city;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.profileimage = profileimage;
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





}
