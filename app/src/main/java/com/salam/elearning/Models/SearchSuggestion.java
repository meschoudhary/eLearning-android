package com.salam.elearning.Models;

public class SearchSuggestion {

    private String serverID;
    private String image;
    private String title;
    private String type;
    private String typeID;

    public SearchSuggestion() {
    }

    public SearchSuggestion(String serverID, String image, String title, String type, String typeID) {
        this.serverID = serverID;
        this.image = image;
        this.title = title;
        this.type = type;
        this.typeID = typeID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }
}
