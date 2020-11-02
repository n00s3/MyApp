package com.example.sign;

import java.util.ArrayList;
import java.util.Date;

//멤버 정보
public class PostInfo {
    private String title;
    private ArrayList<String> contents;
    private String publisher;
    private String name;
    private String sex;
    private String age;
    private String longitude;
    private String laltitude;
    private Date createdAt;
    private String imgurl;
    private String address_lat;
    private String pet_name;
    private String pet_sex;
    private String pet_age;
    private String category;
    private String docID;


    public PostInfo() {
    }

    public PostInfo(String title, String laltitude, String longitude) {
        this.title = title;
        this.laltitude = laltitude;
        this.longitude = longitude;
    }

    public PostInfo(String title, ArrayList<String> contents, String publisher, String laltitude, String longitude, Date createdAt, String address_lat, String pet_name, String pet_age, String pet_sex, String category) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.laltitude = laltitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.address_lat = address_lat;
        this.pet_name = pet_name;
        this.pet_age = pet_age;
        this.pet_sex = pet_sex;
        this.category = category;
    }

    public PostInfo(String title, ArrayList<String> contents, String publisher, String laltitude, String longitude, Date createdAt, String address_lat, String pet_name, String pet_age, String pet_sex, String category, String docID) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.laltitude = laltitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.address_lat = address_lat;
        this.pet_name = pet_name;
        this.pet_age = pet_age;
        this.pet_sex = pet_sex;
        this.category = category;
        this.docID = docID;
    }


    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getContents() {
        return this.contents;
    }
    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public String getPublisher() {
        return this.publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLaltitude() {
        return this.laltitude;
    }
    public void setLaltitude(String laltitude) {
        this.laltitude = laltitude;
    }

    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String Longitude) {
        this.longitude = longitude;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setDate(Date laltitude) {
        this.createdAt = createdAt;
    }


    public String getAddress_lat() {
        return this.address_lat;
    }
    public void setAddress_lat(String address_lat) {
        this.address_lat = address_lat;
    }

    public String getPet_name() {
        return this.pet_name;
    }
    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_sex() {
        return this.pet_sex;
    }
    public void setPet_sex(String address_lat) {
        this.pet_sex = pet_sex;
    }

    public String getPet_age() {
        return this.pet_age;
    }
    public void setPet_age(String address_lat) {
        this.pet_age = pet_age;
    }

    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocID() {
        return this.docID;
    }
    public void setDocID(String docID) {
        this.docID = docID;
    }

    //public String getImgurl() {
    //    return this.imgurl;
    //}
    //public void setImgurl(String imgurl) {
    //    this.imgurl = imgurl;
    //}

}
