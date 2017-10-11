package com.salam.elearning.Models;

import java.io.Serializable;

public class CourseExercise implements Serializable {

    private String name;
    private String attachment;
    private String courseID;

    public CourseExercise(String name, String attachment, String courseID) {
        this.name = name;
        this.attachment = attachment;
        this.courseID = courseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
}
