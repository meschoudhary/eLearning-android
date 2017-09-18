package com.salam.elearning.Models;

import com.orm.SugarRecord;

public class Course extends SugarRecord{

    private String title;
    private String imagePath;
    private String instructor;
    private String serverId;
    private String skill;
    private String viewers;
    private String save;
    private String instructorID;

    public Course() {
    }

    public Course(String title, String imagePath, String instructor, String serverId, String skill, String viewers, String save, String instructorID) {
        this.title = title;
        this.imagePath = imagePath;
        this.instructor = instructor;
        this.serverId = serverId;
        this.skill = skill;
        this.viewers = viewers;
        this.save = save;
        this.instructorID = instructorID;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getServerId() {
        return serverId;
    }

    public String getSkill() {
        return skill;
    }

    public String getViewers() {
        return viewers;
    }

    public String getSave() {
        return save;
    }

    public String getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(String instructorID) {
        this.instructorID = instructorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public void setSave(String save) {
        this.save = save;
    }
}
