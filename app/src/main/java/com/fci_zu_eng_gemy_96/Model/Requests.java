package com.fci_zu_eng_gemy_96.Model;

import java.util.List;

public class Requests {
    private String Phone , Name , TotalPrice ,Address ;
    private List<Order> Foods ;
    private String status ;

    public Requests() {
    }

    public Requests(String phone, String name, String totalPrice, String address, List<Order> foods) {
        this.Phone = phone;
        this.Name = name;
        this.TotalPrice = totalPrice;
        this.Address = address;
        this.Foods = foods;
        this.status = "0";
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public List<Order> getFoods() {
        return Foods;
    }

    public void setFoods(List<Order> foods) {
        Foods = foods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
