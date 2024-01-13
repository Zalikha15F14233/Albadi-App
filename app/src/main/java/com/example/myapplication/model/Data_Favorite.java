package com.example.myapplication.model;

import java.util.List;

public class Data_Favorite {
    String id;

    List<Favorite_Data> favoriteData;

    public Data_Favorite(String id, List<Favorite_Data> favoriteData) {
        this.id = id;
        this.favoriteData = favoriteData;
    }

    public Data_Favorite() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Favorite_Data> getFavoriteData() {
        return favoriteData;
    }

    public void setFavoriteData(List<Favorite_Data> favoriteData) {
        this.favoriteData = favoriteData;
    }
}


// class Favorited {
//
//    String id_item,id_topish,id_user;
//
//    public Favorite_Data(String id_item, String id_topish, String id_user) {
//        this.id_item = id_item;
//        this.id_topish = id_topish;
//        this.id_user = id_user;
//    }
//
//    public Favorite_Data() {
//    }
//
//    public String getId_item() {
//        return id_item;
//    }
//
//    public void setId_item(String id_item) {
//        this.id_item = id_item;
//    }
//
//    public String getId_topish() {
//        return id_topish;
//    }
//
//    public void setId_topish(String id_topish) {
//        this.id_topish = id_topish;
//    }
//
//    public String getId_user() {
//        return id_user;
//    }
//
//    public void setId_user(String id_user) {
//        this.id_user = id_user;
//    }
//}