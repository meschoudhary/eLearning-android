package com.salam.elearning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.salam.elearning.Adapters.CourseAdapter;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity{

    private static final String TAG = "Profile";

    private String userID;

    private Context context;
    private View view;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private CircleImageView toolbarImage;
    private ImageView toolbarBack;

    private User user;

    private CircleImageView mPorfileImage;
    private TextView mProfileUsername;
    private TextView mProfileDesignationCompany;
    private TextView mProfileDescription;

    private ArrayList<Course> allCourses;
    private TextView mAllCoursesByThisUser;
    private RecyclerView mProfileCourses;
    private CourseAdapter mCourseRecylcerAdpter;

    private ArrayList<String> allPublications;
    private TextView mAllPublicationsByThisUser;
    private ArrayAdapter<String> publicationsAdapter;
    private ListView mProfilePublications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        allCourses = new ArrayList<>();
        allPublications = new ArrayList<>();

        userID = getIntent().getStringExtra("userID");

        context = this;
        view = (((Activity) context).findViewById(R.id.profile_activity));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.profile_username_toolbar);
        toolbarBack = toolbar.findViewById(R.id.profile_back);
        toolbarImage = toolbar.findViewById(R.id.toolbar_profile);

        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mPorfileImage = findViewById(R.id.profile_instructor_image);
        mProfileUsername = findViewById(R.id.profile_username);
        mProfileDesignationCompany = findViewById(R.id.profile_designation_company);
        mProfileDescription = findViewById(R.id.profile_description);

        mAllCoursesByThisUser = findViewById(R.id.all_courses_by_this_user);
        mProfileCourses = findViewById(R.id.profile_courses_recycler_view);
        mCourseRecylcerAdpter = new CourseAdapter(context , allCourses, R.layout.cell_saved_course);
        mProfileCourses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mProfileCourses.setAdapter(mCourseRecylcerAdpter);
        mProfileCourses.setNestedScrollingEnabled(false);

        mAllPublicationsByThisUser = findViewById(R.id.all_publications_by_this_user);
        mProfilePublications = findViewById(R.id.profile_publication_view);
        publicationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allPublications);
        mProfilePublications.setAdapter(publicationsAdapter);
        new GetProfile(userID).execute();
    }

    private class GetProfile extends AsyncTask<Void, Void, String>{

        String userID;
        GetProfile(String userID) {
            this.userID = userID;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("type", "1");

            NetworkConnection networkConnection = new NetworkConnection();
            String loginApi = "http://104.131.71.64/admin/api/getuser";
            response = networkConnection.performPostCall(loginApi, params);

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

                        JSONObject data = jsonObject.getJSONObject("response");

                        String imageHost = "http://104.131.71.64";

                        JSONArray courses = data.getJSONArray("courses");
                        JSONArray publications = data.getJSONArray("publications");
                        JSONObject user = data.getJSONObject("user");


                        Picasso.with(context)
                                .load(Uri.parse(imageHost + user.getString("profile_pic")))
                                .placeholder(R.drawable.cover_placeholder)
                                .error(R.drawable.cover_error)
                                .into(mPorfileImage);

                        String username = user.getString("username");
                        String designation = user.getString("designation");
                        String company = user.getString("company");
                        String description = user.getString("description");
                        String designationCompany = "";

                        if(courses.length() > 0){
                            mAllCoursesByThisUser.setText(context.getString(R.string.profile_instructor_id, username));
                        }

                        if(publications.length() > 0){
                            mAllPublicationsByThisUser.setText(context.getString(R.string.profile_publications, username));
                        }

                        if(!designation.isEmpty() && !company.isEmpty())
                            designationCompany = designation + " at " + company;

                        else if(designation.isEmpty() && !company.isEmpty())
                            designationCompany = company;

                        else if(!designation.isEmpty() && company.isEmpty())
                            designationCompany = designation;

                        toolbarTitle.setText(username);
                        Picasso.with(context)
                                .load(Uri.parse(imageHost + user.getString("profile_pic")))
                                .placeholder(R.drawable.cover_placeholder)
                                .error(R.drawable.cover_error)
                                .into(toolbarImage);

                        mProfileUsername.setText(username);
                        mProfileDesignationCompany.setText(designationCompany);
                        mProfileDescription.setText(description);

                        for (int i = 0; i < courses.length(); i++){
                            JSONObject courseData = courses.getJSONObject(i);

                            String courseId = courseData.getString("course_id");
                            String courseTitle = courseData.getString("course_title");
                            String courseImg = imageHost + courseData.getString("course_img");
                            String courseInstructor = courseData.getString("course_instructor");
                            String courseViews = courseData.getString("course_viewers");
                            String courseSkill = courseData.getString("skill");
                            String courseSaved = courseData.getString("course_save");
                            String courseInstructorID = courseData.getString("course_instructor_id");

                            allCourses.add(new Course(courseTitle, courseImg, courseInstructor, courseId, "", courseViews, courseSaved, courseInstructorID));
                        }

                        for (int i = 0; i < publications.length(); i++){
                            String publication_name = (String) publications.get(0);

                            allPublications.add(publication_name);
                        }

                        mCourseRecylcerAdpter.refreshAdapter(allCourses);
                        publicationsAdapter.notifyDataSetChanged();
                        Utils.setListViewHeightBasedOnItems(mProfilePublications);

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

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }
}
