package com.salam.elearning.Models;

import com.orm.SugarRecord;
import java.util.List;

public class User extends SugarRecord {

    private String serverId;
    private String username;
    private String email;
    private String loggedIn;

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


}
