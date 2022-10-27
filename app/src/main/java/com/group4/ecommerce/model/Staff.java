package com.group4.ecommerce.model;

import java.util.ArrayList;

public class Staff {
    String email,fullname,image;
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

    public Staff( String email,String fullname, String image) {
        this.fullname = fullname;
        this.email = email;
        this.image = image;
    }
}
