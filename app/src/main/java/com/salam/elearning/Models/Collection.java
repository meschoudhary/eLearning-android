package com.salam.elearning.Models;

import java.io.Serializable;

public class Collection implements Serializable {

    private String name;
    private String description;
    private String userID;
    private String serverID;

    public Collection() {}

    public Collection(String name, String description, String userID, String serverID) {
        this.name = name;
        this.description = description;
        this.userID = userID;
        this.serverID = serverID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
}
