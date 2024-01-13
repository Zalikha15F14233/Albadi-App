package com.example.myapplication.model;

public class Favorite_Data {

    String id_item,id_topish,id_user;

    public Favorite_Data(String id_item, String id_topish, String id_user) {
        this.id_item = id_item;
        this.id_topish = id_topish;
        this.id_user = id_user;
    }

    public Favorite_Data() {
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public String getId_topish() {
        return id_topish;
    }

    public void setId_topish(String id_topish) {
        this.id_topish = id_topish;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
