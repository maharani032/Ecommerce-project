package com.group4.ecommerce.model;

import java.util.ArrayList;

public class Staff {
    String email,fullname,image,id;
    ArrayList<String> list;
    public Staff(){

    }


    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String> getList() {
        return list;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Staff( String email,String fullname, String image,String id) {
        this.fullname = fullname;
        this.email = email;
        this.image = image;
        this.id= id;
    }
}


