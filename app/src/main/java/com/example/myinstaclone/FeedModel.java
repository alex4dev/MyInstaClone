package com.example.myinstaclone;


import java.util.Date;

public class FeedModel {

    private String author;

    private String description;
    private String location;
    private String imageName;
    private Date date;
    /*
        public FeedModel(Date date, String imageName, String author,  String description, String location){
            this._author = author;
            this._description = description;
            this._location = location;
            this._imageName = imageName;
            this._date = date;
        }*/
    public FeedModel(){}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



}
