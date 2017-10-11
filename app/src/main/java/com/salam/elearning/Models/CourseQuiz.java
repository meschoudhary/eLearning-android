package com.salam.elearning.Models;

import java.io.Serializable;
import java.util.List;

public class CourseQuiz implements Serializable {

    private int quizCount;
    private List<Quiz> quizList;

    public CourseQuiz() {
    }

    public CourseQuiz(int quizCount, List<Quiz> quizList) {
        this.quizCount = quizCount;
        this.quizList = quizList;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }
}
