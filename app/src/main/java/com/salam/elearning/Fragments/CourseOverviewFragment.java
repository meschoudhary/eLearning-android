package com.salam.elearning.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salam.elearning.Adapters.CourseLikeAdapter;
import com.salam.elearning.Adapters.RelatedCourseAdapter;
import com.salam.elearning.Adapters.SkillAdapter;
import com.salam.elearning.CourseActivity;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.CourseChapters;
import com.salam.elearning.Models.CourseExercise;
import com.salam.elearning.Models.CourseSections;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseOverviewFragment extends Fragment {

    private static final String TAG = "OverviewFragment";

    private View fragmentView;
    private Context context;

    private TextView mDuration;
    private TextView mLevel;
    private TextView mDescription;
    private CircleImageView mInstructorImage;
    private TextView mInsructorName;

    private RecyclerView mLikeRecyclerView;
    private CourseLikeAdapter mLikeRecyclerAdapter;
    private TextView mViewers;
    private TextView mLikes;

    private RecyclerView mSkillsRecyclerView;
    private SkillAdapter mSkillAdapter;

    private RecyclerView mRelatedCoursesRecyclerView;
    private RelatedCourseAdapter mRelatedCourseAdapter;

    private Course course;
    private String instructorImage;
    private HashMap<String, String> courseMetaList;
    private ArrayList<CourseExercise> courseExerciseList;
    private ArrayList<Skill> courseSkillList;
    private ArrayList<Course> relatedCourses;
    private boolean courseLikedbyThisUser;
    private ArrayList<User> courseLikedByUsers;

    public CourseOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_course_overview, container, false);

        mDuration = fragmentView.findViewById(R.id.course_duration);
        mLevel = fragmentView.findViewById(R.id.course_level);
        mDescription = fragmentView.findViewById(R.id.course_description);
        mInstructorImage = fragmentView.findViewById(R.id.course_instructor_image);
        mInsructorName = fragmentView.findViewById(R.id.course_instructor);

        mLikeRecyclerView = fragmentView.findViewById(R.id.likes_recycler_view);
        mViewers = fragmentView.findViewById(R.id.course_viewers);
        mLikes = fragmentView.findViewById(R.id.course_likes);

        mSkillsRecyclerView = fragmentView.findViewById(R.id.course_skills);
        mRelatedCoursesRecyclerView = fragmentView.findViewById(R.id.related_courses_recycler_view);

        context = getActivity();

        course = (Course) getArguments().getSerializable("course");
        instructorImage = getArguments().getString("instructorImage");
        courseLikedbyThisUser = getArguments().getBoolean("courseLikedbyThisUser");

        courseMetaList = (HashMap<String, String>) getArguments().getSerializable("courseMetaList");
        courseExerciseList = (ArrayList<CourseExercise>) getArguments().getSerializable("courseExerciseList");
        courseSkillList = (ArrayList<Skill>) getArguments().getSerializable("courseSkillList");
        relatedCourses = (ArrayList<Course>) getArguments().getSerializable("relatedCourses");
        courseLikedByUsers = (ArrayList<User>) getArguments().getSerializable("courseLikedByUsers");

        mLikeRecyclerAdapter = new CourseLikeAdapter(context, courseLikedByUsers);
        mLikeRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mLikeRecyclerView.setAdapter(mLikeRecyclerAdapter);

        mSkillAdapter = new SkillAdapter(context, courseSkillList, R.layout.cell_skill, "", fragmentView);
        mSkillsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mSkillsRecyclerView.setAdapter(mSkillAdapter);

        mRelatedCourseAdapter = new RelatedCourseAdapter(context, relatedCourses);
        mRelatedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRelatedCoursesRecyclerView.setAdapter(mRelatedCourseAdapter);

        Log.d(TAG, "done");

        mDuration.setText(courseMetaList.get("duration"));
        mLevel.setText(courseMetaList.get("level"));
        mDescription.setText(courseMetaList.get("description"));
        mInsructorName.setText(course.getInstructor());

        mViewers.setText(context.getResources().getString(R.string.course_screen_viewers, course.getViewers()));
        mLikes.setText(context.getResources().getString(R.string.course_screen_likes, course.getLikes()));

        Picasso.with(context)
                .load(instructorImage)
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(mInstructorImage);

        return fragmentView;
    }

}
