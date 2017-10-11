package com.salam.elearning.Models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

public class Course extends SugarRecord implements Serializable{

    private String title;
    private String imagePath;
    private String instructor;
    private String serverId;
    private String skill;
    private String viewers;
    private String save;
    private String instructorID;
    private String likes;
    private String duration;
    private String type;
    private String score;

    public Course() {
    }
    public Course(String title, String serverId){
        this.title = title;
        this.serverId = serverId;
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

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public static List<Course> findByTitle(String title){
        return Course.find(Course.class, "title = ?", title);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
