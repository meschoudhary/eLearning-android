package com.salam.elearning;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.salam.elearning.Fragments.CourseContentFragment;
import com.salam.elearning.Fragments.CourseOverviewFragment;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.CourseChapters;
import com.salam.elearning.Models.CourseExercise;
import com.salam.elearning.Models.CourseQuiz;
import com.salam.elearning.Models.CourseSections;
import com.salam.elearning.Models.Quiz;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CourseActivity extends AppCompatActivity {

    private static final String TAG = "CourseActivity";
    private View view;

    private String courseID;
    private String userID;

    private Course course = new Course();
    private String instructorImage;
    private HashMap<String, String> courseMetaList;
    private ArrayList<CourseExercise> courseExerciseList;
    private ArrayList<Skill> courseSkillList;

    private ArrayList<CourseSections> courseContentList = new ArrayList<>();

    private ArrayList<Course> relatedCourses;
    private boolean courseLikedbyThisUser;
    private ArrayList<User> courseLikedByUsers;

    private TextView mCourseName;
    private ImageView mCourseSaved;
    private int courseSaveStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Context context = this;

        view = (((Activity) context).findViewById(R.id.course_activity));
        mCourseName = findViewById(R.id.course_name);
        mCourseSaved = findViewById(R.id.course_saved);

        courseID = getIntent().getStringExtra("courseID");
        userID = getIntent().getStringExtra("userID");

        new GetCourse().execute();
    }

    private class GetCourse extends AsyncTask<Void, Void, String> {

        GetCourse() {
        }

        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("courseID", courseID);
            params.put("userID", userID);

            NetworkConnection networkConnection = new NetworkConnection();
            String getCourseApi = getString(R.string.api_get_course);
            response = networkConnection.performPostCall(getCourseApi, params);

            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            if (!response.isEmpty()) {

                try {

                    Log.d(TAG, response);

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        JSONObject data = jsonObject.getJSONObject("response");

                        String host = "http://104.131.71.64";

                        JSONObject courseData = data.getJSONObject("course");

                        course.setServerId(courseData.getString("course_id"));
                        course.setTitle(courseData.getString("course_title"));
                        course.setImagePath(host + courseData.getString("course_img"));
                        course.setInstructor(courseData.getString("course_instructor"));
                        course.setInstructorID(courseData.getString("course_instructor_id"));
                        course.setViewers(courseData.getString("course_viewers"));
                        course.setSave(courseData.getString("course_save"));
                        courseSaveStatus = Integer.parseInt(courseData.getString("course_save"));
                        course.setLikes(courseData.getString("course_likes"));
                        courseLikedbyThisUser = (courseData.getString("course_like").equalsIgnoreCase("1"));
                        instructorImage = host + courseData.getString("instructor_profile");

                        JSONArray courseLikesData = courseData.getJSONArray("course_like_data");
                        courseLikedByUsers = new ArrayList<>();
                        for (int i = 0; i < courseLikesData.length(); i++){
                            JSONObject userData = courseLikesData.getJSONObject(i);

                            User user = new User();
                            user.setUsername(userData.getString("username"));
                            user.setImage(host + userData.getString("profile_pic"));
                            user.setDesignation(userData.getString("designation"));
                            user.setCompany(userData.getString("company"));
                            user.setServerId(userData.getString("id"));

                            courseLikedByUsers.add(user);
                        }


                        JSONObject courseMetaData = data.getJSONObject("courseMeta");
                        courseMetaList = new HashMap<>();
                        courseMetaList.put("level", courseMetaData.getString("level"));
                        courseMetaList.put("duration", courseMetaData.getString("duration"));
                        courseMetaList.put("description", courseMetaData.getString("description"));

                        JSONArray courseSkills = courseMetaData.getJSONArray("skill");
                        courseSkillList = new ArrayList<>();
                        for (int i = 0; i < courseSkills.length(); i++){
                            courseSkillList.add(new Skill("", courseSkills.getString(i)));
                        }

                        JSONArray courseExerciseAllData = data.getJSONArray("courseExercise");
                        courseExerciseList = new ArrayList<>();
                        for (int i = 0; i < courseExerciseAllData.length(); i++){

                            JSONObject courseExerciseData = courseExerciseAllData.getJSONObject(i);
                            courseExerciseList.add(new CourseExercise(courseExerciseData.getString("attachment_name"),
                                    courseExerciseData.getString("attachment"), courseExerciseData.getString("course_id")));

                        }

                        JSONArray courseSectionsAllData = data.getJSONArray("sections");
                        courseContentList = new ArrayList<>();

                        for (int i = 0; i < courseSectionsAllData.length(); i++){

                            JSONObject courseSection = courseSectionsAllData.getJSONObject(i);

                            JSONArray courseChapterAllData = courseSection.getJSONArray("chapter");
                            JSONArray courseQuizAllData = courseSection.getJSONArray("quiz");

                            ArrayList<Object> sectionContentList = new ArrayList<>();
                            for (int j = 0; j < courseChapterAllData.length(); j++){

                                JSONObject courseChapter = courseChapterAllData.getJSONObject(j);

                                String chapterName = courseChapter.getString("name");
                                String chapterDuration = courseChapter.getString("duration");
                                String chapterVideo = host + courseChapter.getString("video_url");
                                String chapterTranscript = host + courseChapter.getString("transcript");
                                String chapterCourseID = courseChapter.getString("course_id");
                                String chapterSectionID = courseChapter.getString("course_section_id");
                                String chapterServerID = courseChapter.getString("id");
                                boolean chapterCompleted = courseChapter.getBoolean("completed");


                                sectionContentList.add(new CourseChapters(chapterName, chapterDuration, chapterVideo,
                                        chapterTranscript, chapterSectionID, chapterCourseID, chapterServerID, chapterCompleted));
                            }

                            ArrayList<Quiz> quizArrayList = new ArrayList<>();
                            for (int k = 0; k < courseQuizAllData.length(); k++){

                                JSONObject courseQuizData = courseQuizAllData.getJSONObject(k);

                                Quiz quiz = new Quiz();

                                quiz.setServerID(courseQuizData.getString("id"));
                                quiz.setCourseID(courseQuizData.getString("course_id"));
                                quiz.setSectionID(courseQuizData.getString("section_id"));
                                quiz.setChapterID(courseQuizData.getString("related_chapter_id"));
                                quiz.setQuestion(courseQuizData.getString("question"));
                                quiz.setOption1(courseQuizData.getString("option_1"));
                                quiz.setOption2(courseQuizData.getString("option_2"));
                                quiz.setOption3(courseQuizData.getString("option_3"));
                                quiz.setOption4(courseQuizData.getString("option_4"));
                                quiz.setAnswer(courseQuizData.getString("answer"));
                                quiz.setChapterTitle(courseQuizData.getString("chapter_title"));
                                quiz.setChapterDuration(courseQuizData.getString("chapter_duration"));
                                quiz.setChapterImage(courseQuizData.getString("chapter_image"));

                                quizArrayList.add(quiz);
                            }

                            CourseQuiz courseQuiz = new CourseQuiz();
                            courseQuiz.setQuizCount(quizArrayList.size());
                            courseQuiz.setQuizList(quizArrayList);

                            sectionContentList.add(courseQuiz);

                            CourseSections courseSections = new CourseSections(courseSection.getString("name"), course.getServerId(), sectionContentList, courseQuizAllData.length());

                            courseContentList.add(courseSections);
                        }

                        JSONArray relatedCoursesAllData = data.getJSONArray("relatedCourses");
                        relatedCourses = new ArrayList<>();
                        for (int i = 0; i < relatedCoursesAllData.length(); i++){

                            JSONObject relatedCourse = relatedCoursesAllData.getJSONObject(i);

                            Course course = new Course();
                            course.setServerId(relatedCourse.getString("course_id"));
                            course.setTitle(relatedCourse.getString("course_title"));
                            course.setImagePath(host + relatedCourse.getString("course_img"));
                            course.setInstructor(relatedCourse.getString("course_instructor"));
                            course.setInstructorID(relatedCourse.getString("course_instructor_id"));
                            course.setViewers(relatedCourse.getString("course_viewers"));
                            course.setSave(relatedCourse.getString("course_save"));
                            course.setDuration(courseMetaList.get("duration"));
                            relatedCourses.add(course);
                        }

                        Log.d(TAG, "all_done");

                        updateUI();

                    } else {

                        Utils.showSnackBar(view, error, Snackbar.LENGTH_SHORT);

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    Utils.showSnackBar(view, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            } else {
                Utils.showSnackBar(view, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void updateUI() {

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putSerializable("course", course);
        bundle.putString("instructorImage", instructorImage);
        bundle.putSerializable("courseExerciseList", courseExerciseList);
        bundle.putSerializable("courseSkillList", courseSkillList);
        bundle.putSerializable("courseMetaList", courseMetaList);
        bundle.putSerializable("courseContentList", courseContentList);
        bundle.putSerializable("relatedCourses", relatedCourses);
        bundle.putBoolean("courseLikedbyThisUser", courseLikedbyThisUser);
        bundle.putSerializable("courseLikedByUsers", courseLikedByUsers);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(CourseActivity.this)
                .add(R.string.course_overview_fragment, CourseOverviewFragment.class, bundle )
                .add(R.string.course_content_fragment, CourseContentFragment.class, bundle)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        final SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        mCourseName.setText(course.getTitle());

        int drawable = (course.getSave().equalsIgnoreCase("1")) ? R.drawable.saved_course : R.drawable.save_course;

        Picasso.with(CourseActivity.this)
                .load(drawable)
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(mCourseSaved);

        mCourseSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SaveCourse(userID, courseSaveStatus+"", courseID).execute();
            }
        });

    }

    private class SaveCourse extends AsyncTask<Void, Void, String> {

        String userID;
        String saved;
        String courseID;
        int position;

        SaveCourse(String userID, String saved, String courseID) {
            this.userID = userID;
            this.courseID = courseID;
            this.saved = (saved.equalsIgnoreCase("1")) ? "0" : "1";
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("courseID", courseID);
            params.put("save", saved);

            NetworkConnection networkConnection = new NetworkConnection();
            String saveCourseAPI = getString(R.string.api_save_course);
            response = networkConnection.performPostCall(saveCourseAPI, params);

            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            if(!response.isEmpty()){

                try {

                    Log.d(TAG, response);

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        int drawable = (saved.equalsIgnoreCase("0")) ? R.drawable.save_course : R.drawable.saved_course;

                        courseSaveStatus = (saved.equalsIgnoreCase("0")) ? 0 : 1;

                        Picasso.with(CourseActivity.this)
                                .load(drawable)
                                .placeholder(R.drawable.cover_placeholder)
                                .error(R.drawable.cover_error)
                                .into(mCourseSaved);

                        String responseSaved = jsonObject.getString("response");

                        Utils.showSnackBar(view, responseSaved, Snackbar.LENGTH_SHORT);

                    } else {

                        Utils.showSnackBar(view, error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(view, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(view, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }
}
