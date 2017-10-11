package com.salam.elearning.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.salam.elearning.ProfileActivity;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseOverviewFragment extends Fragment {

    private static final String TAG = "OverviewFragment";

    private Context context;

    private Course course;

    public CourseOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_course_overview, container, false);

        TextView mDuration = fragmentView.findViewById(R.id.course_duration);
        TextView mLevel = fragmentView.findViewById(R.id.course_level);
        TextView mDescription = fragmentView.findViewById(R.id.course_description);
        LinearLayout mCourseInstructorContainer = fragmentView.findViewById(R.id.course_instructor_container);
        CircleImageView mInstructorImage = fragmentView.findViewById(R.id.course_instructor_image);
        TextView mInsructorName = fragmentView.findViewById(R.id.course_instructor);

        RecyclerView mLikeRecyclerView = fragmentView.findViewById(R.id.likes_recycler_view);
        TextView mViewers = fragmentView.findViewById(R.id.course_viewers);
        TextView mLikes = fragmentView.findViewById(R.id.course_likes);

        RecyclerView mSkillsRecyclerView = fragmentView.findViewById(R.id.course_skills);
        RecyclerView mRelatedCoursesRecyclerView = fragmentView.findViewById(R.id.related_courses_recycler_view);

        context = getActivity();

        course = (Course) getArguments().getSerializable("course");
        String instructorImage = getArguments().getString("instructorImage");
        boolean courseLikedbyThisUser = getArguments().getBoolean("courseLikedbyThisUser");

        HashMap<String, String> courseMetaList = (HashMap<String, String>) getArguments().getSerializable("courseMetaList");
        ArrayList<CourseExercise> courseExerciseList = (ArrayList<CourseExercise>) getArguments().getSerializable("courseExerciseList");
        ArrayList<Skill> courseSkillList = (ArrayList<Skill>) getArguments().getSerializable("courseSkillList");
        ArrayList<Course> relatedCourses = (ArrayList<Course>) getArguments().getSerializable("relatedCourses");
        ArrayList<User> courseLikedByUsers = (ArrayList<User>) getArguments().getSerializable("courseLikedByUsers");

        CourseLikeAdapter mLikeRecyclerAdapter = new CourseLikeAdapter(context, courseLikedByUsers);
        mLikeRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mLikeRecyclerView.setAdapter(mLikeRecyclerAdapter);

        SkillAdapter mSkillAdapter = new SkillAdapter(context, courseSkillList, R.layout.cell_skill, "", fragmentView);
        mSkillsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mSkillsRecyclerView.setAdapter(mSkillAdapter);

        RelatedCourseAdapter mRelatedCourseAdapter = new RelatedCourseAdapter(context, relatedCourses);
        mRelatedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRelatedCoursesRecyclerView.setAdapter(mRelatedCourseAdapter);

        Log.d(TAG, "done");

        mDuration.setText(courseMetaList.get("duration"));
        mLevel.setText(courseMetaList.get("level"));
        mDescription.setText(courseMetaList.get("description"));
        mInsructorName.setText(course.getInstructor());

        mCourseInstructorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userID", course.getInstructorID());
                context.startActivity(intent);
            }
        });

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
