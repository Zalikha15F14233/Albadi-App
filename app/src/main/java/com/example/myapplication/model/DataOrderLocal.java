package com.example.myapplication.model;

public class DataOrderLocal {

    String id,prodectname,prodectPrice,prodectDiscount,prodectamount,prodectid,status,imageurl;

    int totalprice,count;

    public DataOrderLocal(int totalprice, int count) {
        this.totalprice = totalprice;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public DataOrderLocal(String id, String prodectname, String prodectPrice, String prodectDiscount, String prodectamount, String prodectid) {
        this.id = id;
        this.prodectname = prodectname;
        this.prodectPrice = prodectPrice;
        this.prodectDiscount = prodectDiscount;
        this.prodectamount = prodectamount;
        this.prodectid = prodectid;
    }

    public DataOrderLocal(String id, String prodectname, String prodectPrice, String prodectDiscount, String prodectamount, String prodectid, String status, String imageurl, int totalprice, int count) {
        this.id = id;
        this.prodectname = prodectname;
        this.prodectPrice = prodectPrice;
        this.prodectDiscount = prodectDiscount;
        this.prodectamount = prodectamount;
        this.prodectid = prodectid;
        this.status = status;
        this.imageurl = imageurl;
        this.totalprice = totalprice;
        this.count = count;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getProdectid() {
        return prodectid;
    }

    public void setProdectid(String prodectid) {
        this.prodectid = prodectid;
    }

    public DataOrderLocal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProdectname() {
        return prodectname;
    }

    public void setProdectname(String prodectname) {
        this.prodectname = prodectname;
    }

    public String getProdectPrice() {
        return prodectPrice;
    }

    public void setProdectPrice(String prodectPrice) {
        this.prodectPrice = prodectPrice;
    }

    public String getProdectDiscount() {
        return prodectDiscount;
    }

    public void setProdectDiscount(String prodectDiscount) {
        this.prodectDiscount = prodectDiscount;
    }

    public String getProdectamount() {
        return prodectamount;
    }

    public void setProdectamount(String prodectamount) {
        this.prodectamount = prodectamount;
    }
}

