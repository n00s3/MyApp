package com.example.sign;

//멤버 정보
public class User {

    private String name;
    private String phone;
    private String addr;
    private String photoUrl;
    private String latitude;
    private String longitude;

    public User() {

    }

    public User(String name, String phone, String photoUrl, String latitude, String longitude) {
        this.name = name;
        this.phone = phone;
        //this.addr = addr;
        this.photoUrl = photoUrl;
        this.latitude =latitude;
        this.longitude = longitude;
    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public User(String name, String phone, String latitude, String longitude) {
        this.name = name;
        this.phone = phone;
        this.latitude =latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLatitude() {
        return this.latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


/*
    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
    */

}
