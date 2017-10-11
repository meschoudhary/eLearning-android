package com.salam.elearning.Models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

public class User extends SugarRecord implements Serializable {

    private String serverId;
    private String username;
    private String email;
    private String loggedIn;
    private String image;
    private String designation;
    private String company;

    public String getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(String passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    private String passwordChanged;

    public User(){

    }

    public User(String serverId, String username, String email, String loggedIn) {
        this.serverId = serverId;
        this.username = username;
        this.email = email;
        this.loggedIn = loggedIn;
    }

    public String getServerId() {
        return serverId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getLoggedIn() {
        return loggedIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLoggedIn(String loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public static List<User> getLoggedInUser(){
        return User.find(User.class, "logged_in = ?", "1");
    }

    public static List<User> findByServerId(String id){
        return User.find(User.class, "server_id = ?", id);
    }

    public static List<User> findByEmail(String email){
        return User.find(User.class, "email = ?", email);
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
