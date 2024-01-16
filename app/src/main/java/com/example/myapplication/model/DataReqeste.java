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
    List<DataOrderLocal> products;

    public List<DataOrderLocal> getProducts() {
        return products;
    }

    public void setProducts(List<DataOrderLocal> products) {
        this.products = products;
    }

    public DataReqeste() {
    }

//    cardForm.getCardNumber();
//cardForm.getExpirationMonth();
//cardForm.getExpirationYear();
//cardForm.getCvv();
//cardForm.getCardholderName();
//cardForm.getPostalCode();
//cardForm.getCountryCode();
//cardForm.getMobileNumber();

    String cardNumber,expirationMonth,expirationYear,cvv,cardholderNam,postalCode,countryCode,mobileNumber;
    public DataReqeste(String total_due, String total_decount, String iduser,
                       String address, String total, List<DataOrderLocal> products,
                       String cardNumber,String expirationMonth,String expirationYear,String cvv,
                       String cardholderNam,String postalCode,String countryCode,String mobileNumber
                       ) {
        this.iduser = iduser;
        this.address = address;
        this.total = total;
        this.products = products;
        this.status="0";
        this.total_decount=total_decount;
        this.total_due=total_due;
        this.location_driver="";
        this.cardNumber=cardNumber;
        this.expirationMonth=expirationMonth;
        this.expirationYear=expirationYear;
        this.cvv=cvv;
        this.cardholderNam=cardholderNam;this.postalCode=postalCode;this.countryCode=countryCode;
        this.mobileNumber=mobileNumber;
    }

    public String getLocation_driver() {
        return location_driver;
    }

    public void setLocation_driver(String location_driver) {
        this.location_driver = location_driver;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardholderNam() {
        return cardholderNam;
    }

    public void setCardholderNam(String cardholderNam) {
        this.cardholderNam = cardholderNam;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

}
