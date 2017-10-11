package com.salam.elearning.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salam.elearning.Adapters.UserLearningHistoryAdapter;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.Collection;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.NetworkUtils;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile";
    private View fragmentView;
    private Context context;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RelativeLayout mProfileScreenHeadingView;
    private CircleImageView mProfilePic;
    private TextView mUsername;
    private TextView mSkills;

    private ProgressBar mProgressBar;

    private UserLearningHistoryAdapter userLearningHistoryAdapter;
    private ArrayList<Course> savedCourses;
    private ArrayList<Course> historyCourses;

    private ArrayList<Collection> collections;

    private HashMap<String, ArrayList<Course>> collectionCourses;

    private ArrayList<Object> allData = new ArrayList<>();

    private String userID = "";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity();

        mSwipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);

        mProfileScreenHeadingView = fragmentView.findViewById(R.id.profile_screen_heading_view);
        mProfilePic = fragmentView.findViewById(R.id.profile_profile_image);
        mUsername = fragmentView.findViewById(R.id.username);
        mSkills = fragmentView.findViewById(R.id.skills);

        mProgressBar = fragmentView.findViewById(R.id.progress_bar);

        final List<User> user = User.getLoggedInUser();
        userID = user.get(0).getServerId();

        mUsername.setText(user.get(0).getUsername());

        mProfileScreenHeadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("userID", user.get(0).getServerId());

                StudentFragment fragment2 = new StudentFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0);
                fragmentTransaction.replace(R.id.student_fragment, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragment2.setArguments(args);
                fragmentTransaction.commit();
            }
        });

        RecyclerView mUserLearningRecyclerView = fragmentView.findViewById(R.id.user_learning_recycler_view);
        userLearningHistoryAdapter = new UserLearningHistoryAdapter(getActivity() , allData, collectionCourses,  userID, fragmentView);
        mUserLearningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserLearningRecyclerView.setAdapter(userLearningHistoryAdapter);

        savedCourses = new ArrayList<Course>();
        historyCourses = new ArrayList<>();
        collections = new ArrayList<>();

        collectionCourses = new HashMap<>();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        new GetUserData(user.get(0).getServerId()).execute();

        return fragmentView;
    }

    private void refreshItems() {

        // Load items

        savedCourses = new ArrayList<Course>();
        collections = new ArrayList<>();
        historyCourses = new ArrayList<>();

        allData = new ArrayList<>();
        collectionCourses = new HashMap<>();

        new GetUserData(userID).execute();

        // Load complete
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class GetUserData extends AsyncTask<Void, Void, String> {

        String userID;

        GetUserData(String userID) {
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

                Log.d(TAG, "sending request");

                NetworkConnection networkConnection = new NetworkConnection();
                String loginApi = context.getString(R.string.api_get_user_data);
                response = networkConnection.performPostCall(loginApi, params);
                Log.d(TAG, "get response");
                Log.d("response", response);
            } else {
                Utils.showSnackBar(fragmentView, "You need to have an active internet connection", Snackbar.LENGTH_SHORT);
            }
            return response;

        }

        @Override
        protected void onPostExecute(String response) {

            if(!response.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    String imageHost = "http://104.131.71.64";
                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        Log.d(TAG, response);
                        JSONObject responseData = jsonObject.getJSONObject("response");

                        String skills = responseData.getString("skills");
                        String profilePic = imageHost + responseData.getString("profilePic");

                        if(skills.equalsIgnoreCase("1")){
                            mSkills.setText(skills + " skill");
                        }else {
                            mSkills.setText(skills + " skills");
                        }

                        Picasso.with(context)
                                .load(Uri.parse(profilePic))
                                .placeholder(R.drawable.cover_placeholder)
                                .error(R.drawable.cover_error)
                                .into(mProfilePic);

                        JSONArray courses = responseData.getJSONArray("saved");
                        JSONArray history = responseData.getJSONArray("history");
                        JSONArray collection = responseData.getJSONArray("collection");

                        for (int i = 0; i < courses.length(); i++){

                            JSONObject courseData = courses.getJSONObject(i);

                            String courseId = courseData.getString("course_id");
                            String courseTitle = courseData.getString("course_title");
                            String courseImg = imageHost + courseData.getString("course_img");
                            String courseInstructor = courseData.getString("course_instructor");
                            String courseViews = courseData.getString("course_viewers");
                            String courseSaved = courseData.getString("course_save");
                            String courseInstructorID = courseData.getString("course_instructor_id");

                            savedCourses.add(new Course(courseTitle, courseImg, courseInstructor, courseId, "", courseViews, courseSaved, courseInstructorID));
                        }

                        allData.add(savedCourses);

                        for (int i = 0; i < history.length(); i++){

                            JSONObject courseData = history.getJSONObject(i);

                            String courseId = courseData.getString("course_id");
                            String courseTitle = courseData.getString("course_title");
                            String courseImg = imageHost + courseData.getString("course_img");
                            String courseInstructor = courseData.getString("course_instructor");
                            String courseViews = courseData.getString("course_viewers");
                            String courseSaved = courseData.getString("course_save");
                            String courseInstructorID = courseData.getString("course_instructor_id");

                            Course course = new Course(courseTitle, courseImg, courseInstructor, courseId, "", courseViews, courseSaved, courseInstructorID);

                            if(courseData.getString("score").equalsIgnoreCase("0")){
                                course.setScore("");
                            }else{
                                course.setScore(courseData.getString("score")  + "%");
                            }

                            historyCourses.add(course);
                        }

                        allData.add(historyCourses);

                        /*
                        now for adding collections
                        collections = A basic arraylist with collection objects in it.
                        collectionCourseList = A basic arraylist with all courses of a particular collection in it
                        collectionCourses = A hashmap with collectionServerID as key and collectionCourseList as value.

                        */
                        for (int i = 0; i < collection.length(); i++){

                            JSONObject courseData = collection.getJSONObject(i);

                            String collectionTitle = courseData.getString("collection_name");
                            String collectionDescription = courseData.getString("collection_description");
                            String collectionId = courseData.getString("collection_id");

                            JSONArray allCollectionData = courseData.getJSONArray("collection_data");

                            ArrayList<Course> collectionCourseList = new ArrayList<>();
                            for (int j = 0; j < allCollectionData.length(); j++){

                                JSONObject collectionCourse = allCollectionData.getJSONObject(j);

                                String courseId = collectionCourse.getString("course_id");
                                String courseTitle = collectionCourse.getString("course_title");
                                String courseImg = imageHost + collectionCourse.getString("course_img");
                                String courseInstructor = collectionCourse.getString("course_instructor");
                                String courseViews = collectionCourse.getString("course_viewers");
                                String courseSaved = collectionCourse.getString("course_save");
                                String courseInstructorID = collectionCourse.getString("course_instructor_id");

                                collectionCourseList.add(new Course(courseTitle, courseImg, courseInstructor, courseId, "", courseViews, courseSaved, courseInstructorID));

                            }

                            collectionCourses.put(collectionId, collectionCourseList);

                            collections.add(new Collection(collectionTitle, collectionDescription, userID, collectionId));
                        }

                        allData.add(collections);

                    } else {
                        Log.d(TAG, error);
                        Utils.showSnackBar(fragmentView, error, Snackbar.LENGTH_SHORT);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showSnackBar(fragmentView, e.getMessage(), Snackbar.LENGTH_SHORT);
                }

                userLearningHistoryAdapter.refreshAdapter(allData, collectionCourses);
            }else{
                Log.d(TAG, response);
                Utils.showSnackBar(fragmentView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
