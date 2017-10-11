package com.salam.elearning.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CourseChapters implements Serializable {

    private String name;
    private String duration;
    private String video;
    private String transcript;
    private String sectionID;
    private String courseID;
    private String serverID;
    private boolean completed;

    public CourseChapters(String name, String duration, String video, String transcript, String sectionID, String courseID, String serverID, boolean completed) {
        this.name = name;
        this.duration = duration;
        this.video = video;
        this.transcript = transcript;
        this.sectionID = sectionID;
        this.courseID = courseID;
        this.serverID = serverID;
        this.completed = completed;
    }

    protected CourseChapters(Parcel in) {
        name = in.readString();
    }

    /*public static final Creator<CourseChapters> CREATOR = new Creator<CourseChapters>() {
        @Override
        public CourseChapters createFromParcel(Parcel in) {
            return new CourseChapters(in);
        }

        @Override
        public CourseChapters[] newArray(int size) {
            return new CourseChapters[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
