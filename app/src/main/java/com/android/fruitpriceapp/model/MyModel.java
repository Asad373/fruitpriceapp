package com.android.fruitpriceapp.model;

public class MyModel {
    String name;
    String des;
    String id;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    String price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public MyModel() {

    }


    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
