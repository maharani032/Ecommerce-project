package com.group4.ecommerce.model;

public class Auth {
    String email,phone,password,role,id;

    public Auth(){

    }


    public Auth(String email, String phone, String password, String role,String id) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }
}
