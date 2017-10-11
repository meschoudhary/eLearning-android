package com.salam.elearning.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.salam.elearning.Adapters.CourseAdapter;
import com.salam.elearning.LoginActivity;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.salam.elearning.SignUpActivity;
import com.salam.elearning.Utils.EndlessRecyclerViewScrollListener;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.NetworkUtils;
import com.salam.elearning.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private static String TAG = "HomeFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CourseAdapter courseRecylcerAdpter;
    private ArrayList<Course> allCourses = new ArrayList<Course>();

    private ProgressBar mProgressBar;
    private String userID;

    private int offset = 0;

    private View rootView;
    private Context context;

    private EndlessRecyclerViewScrollListener scrollListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        mProgressBar = rootView.findViewById(R.id.progress_bar);

        context = getActivity();

        List<User> users = User.getLoggedInUser();
        userID = users.get(0).getServerId();

        allCourses = new ArrayList<Course>();
        RecyclerView courseRecyclerView = rootView.findViewById(R.id.all_courses);
        courseRecylcerAdpter = new CourseAdapter(getActivity() , allCourses, userID, R.layout.cell_course, rootView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        courseRecyclerView.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                refreshItems();
            }
        };

        courseRecyclerView.addOnScrollListener(scrollListener);

        courseRecyclerView.setAdapter(courseRecylcerAdpter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                allCourses = new ArrayList<Course>();
                refreshItems();
            }
        });

        new GetCourses(userID).execute();

        return rootView;
    }

    private void refreshItems() {

        // Load items
        offset++;

        new GetCourses(userID).execute();

        // Load complete
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class GetCourses extends AsyncTask<Void, Void, String>{

        String userID;

        GetCourses(String userID) {
            this.userID = userID;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String response = "";
            if (NetworkUtils.isWifiConnected(context) || NetworkUtils.isMobileConnected(context)) {

                HashMap<String, String> params = new HashMap<>();
                String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";

                params.put("apiCsrfKey", csrfKey);
                params.put("userID", userID);
                params.put("offset", String.valueOf(offset));

                NetworkConnection networkConnection = new NetworkConnection();
                String loginApi = context.getString(R.string.api_get_all_courses);
                response = networkConnection.performPostCall(loginApi, params);
            } else {
                Utils.showSnackBar(rootView, "You need to have an active internet connection", Snackbar.LENGTH_SHORT);
            }
            return response;

        }

        @Override
        protected void onPostExecute(String response) {

            if(!response.isEmpty()) {

                try {
                    Log.d(TAG, response);
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    String imageHost = "http://104.131.71.64";
                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        JSONArray jsonArray = jsonObject.getJSONArray("response");

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject courseData = jsonArray.getJSONObject(i);

                            String courseId = courseData.getString("course_id");
                            String courseTitle = courseData.getString("course_title");
                            String courseImg = imageHost + courseData.getString("course_img");
                            String courseInstructor = courseData.getString("course_instructor");
                            String courseViews = courseData.getString("course_viewers");
                            String courseSkill = courseData.getString("skill");
                            String courseSaved = courseData.getString("course_save");
                            String courseInstructorID = courseData.getString("course_instructor_id");

                            allCourses.add(new Course(courseTitle, courseImg, courseInstructor, courseId, courseSkill, courseViews, courseSaved, courseInstructorID));
                        }
                    }else if(status.equalsIgnoreCase("204")){
                        error += " Refresh again.";
                        Utils.showSnackBar(rootView, error, Snackbar.LENGTH_SHORT);
                        offset = -1;
                    } else {

                        Utils.showSnackBar(rootView, error, Snackbar.LENGTH_SHORT);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                courseRecylcerAdpter.refreshAdapter(allCourses);
                scrollListener.resetState();
            }else{
                Utils.showSnackBar(rootView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
