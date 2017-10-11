package com.salam.elearning.Models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.io.Serializable;
import java.util.List;

public class CourseSections implements Serializable{

    private String name;
    private String courseID;
    private String duration;
    private List<Object> sectionContent;
    private int quizCount;

    public CourseSections(String name, String courseID, List<Object> sectionContent, int quizCount) {
        this.name = name;
        this.courseID = courseID;
        this.sectionContent = sectionContent;
        this.quizCount = quizCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Object> getSectionContent() {
        return sectionContent;
    }

    public void setSectionContent(List<Object> sectionContent) {
        this.sectionContent = sectionContent;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }
}

