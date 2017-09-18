package com.salam.elearning.Models;

import com.orm.SugarRecord;


public class Skill extends SugarRecord {

    private  String serverId;
    private  String name;

    public Skill() {

    }

    public Skill(String serverId, String name) {
        this.serverId = serverId;
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
