package com.salam.elearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;

import com.salam.elearning.Adapters.CollectionAdapter;
import com.salam.elearning.Adapters.CourseAdapter;
import com.salam.elearning.Models.Collection;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.TypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {


    private RecyclerView mCollectionDetailsRecyclerView;
    private CourseAdapter mCourseAdapter;

    private Collection collection;
    private ArrayList<Course> collectionCourse;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        List<User> users = User.getLoggedInUser();
        userID = users.get(0).getServerId();

        collection = (Collection) getIntent().getExtras().getSerializable("collection");
        collectionCourse = (ArrayList<Course>) getIntent().getExtras().getSerializable("collectionData");
        mCollectionDetailsRecyclerView = findViewById(R.id.collection_details);

        SpannableString actionBarTitle = new SpannableString(collection.getName());
        actionBarTitle.setSpan(new TypefaceSpan(this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(actionBarTitle);


        actionBarTitle = new SpannableString(collection.getDescription());
        actionBarTitle.setSpan(new TypefaceSpan(this, "SourceSansPro-Light.ttf"), 0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setSubtitle(actionBarTitle);

        mCourseAdapter = new CourseAdapter(this, collectionCourse, userID, R.layout.cell_course, findViewById(android.R.id.content) );
        mCollectionDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionDetailsRecyclerView.setAdapter(mCourseAdapter);
    }
}
