package com.example.myapplication.model;

import java.util.List;

public class DataReqeste {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String iduser,address,total,status,total_decount ,total_due,location_driver;
    List<DataOrderLocal> food;

    public DataReqeste() {
    }

    public DataReqeste(String total_due, String total_decount, String iduser,
                       String address, String total, List<DataOrderLocal> food) {
        this.iduser = iduser;
        this.address = address;
        this.total = total;
        this.food = food;
        this.status="0";
        this.total_decount=total_decount;
        this.total_due=total_due;
        this.location_driver="";
    }

    public String getTotal_decount() {
        return total_decount;
    }

    public String getTotal_due() {
        return total_due;
    }

    public void setTotal_due(String total_due) {
        this.total_due = total_due;
    }

    public void setTotal_decount(String total_decount) {
        this.total_decount = total_decount;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<DataOrderLocal> getFood() {
        return food;
    }

    public void setFood(List<DataOrderLocal> food) {
        this.food = food;
    }
}
