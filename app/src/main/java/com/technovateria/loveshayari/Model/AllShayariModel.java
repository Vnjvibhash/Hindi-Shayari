package com.technovateria.loveshayari.Model;

public class AllShayariModel {

    String id;
    String data;

    public AllShayariModel() {
    }

    public AllShayariModel(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
