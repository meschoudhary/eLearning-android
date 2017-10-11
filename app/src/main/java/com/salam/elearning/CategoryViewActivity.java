package com.salam.elearning;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.salam.elearning.Adapters.CategoryCourseAdapter;
import com.salam.elearning.Adapters.RelatedCourseAdapter;
import com.salam.elearning.Adapters.RelatedSubCategoryAdapter;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.SubCategory;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.TypefaceSpan;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewActivity extends AppCompatActivity{

    private static final String TAG = "CategoryView";
    private View view;

    private String subCategoryName;

    private ArrayList<SubCategory> relatedSubCategoriesList = new ArrayList<>();
    private RelatedSubCategoryAdapter relatedSubCategoryAdapter;

    private TextView mCategoryCourseHeading;
    private ArrayList<Course> matchedCourseSubCategory = new ArrayList<>();
    private CategoryCourseAdapter matchedCourseSubCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);

        view = findViewById(R.id.activity_category_view);

        List<User> users = User.getLoggedInUser();
        User user = users.get(0);

        String subCategoryID = getIntent().getExtras().getString("subCategoryID");
        subCategoryName = getIntent().getExtras().getString("subCategoryName");

        SpannableString subCategoryNameTitle = new SpannableString(subCategoryName);
        subCategoryNameTitle.setSpan(new TypefaceSpan(this, "SourceSansPro-Regular.ttf"), 0, subCategoryNameTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedappbar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorAccent));
        collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorAccent));

        collapsingToolbar.setTitle(subCategoryNameTitle);

        relatedSubCategoryAdapter = new RelatedSubCategoryAdapter(this, relatedSubCategoriesList);

        RecyclerView relatedSubCategoriesRecyclerView = findViewById(R.id.related_sub_categories);
        relatedSubCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedSubCategoriesRecyclerView.setAdapter(relatedSubCategoryAdapter);

        mCategoryCourseHeading = findViewById(R.id.courses_matched_sub_category_view);
        matchedCourseSubCategoryAdapter = new CategoryCourseAdapter(this, matchedCourseSubCategory, user.getServerId());
        RecyclerView mMatchedCourseSubCategoryRecyclerView = findViewById(R.id.courses_matched_sub_category_view_recycler_view);
        mMatchedCourseSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMatchedCourseSubCategoryRecyclerView.setAdapter(matchedCourseSubCategoryAdapter);

        new GetSubcategoryData(user.getServerId(), subCategoryID).execute();
    }

    private class GetSubcategoryData extends AsyncTask<Void, Void, String> {

        String userID;
        String subCategoryID;

        GetSubcategoryData(String userID, String subCategoryID) {
            this.userID = userID;
            this.subCategoryID = subCategoryID;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("subCategoryID", subCategoryID);

            NetworkConnection networkConnection = new NetworkConnection();
            String getSubcategoryDataApi = getString(R.string.api_get_sub_category_data);
            response = networkConnection.performPostCall(getSubcategoryDataApi, params);

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

                        JSONArray relatedSubCategories = data.getJSONArray("relatedSubCategories");
                        JSONArray subCategoryCourses = data.getJSONArray("subCategoryCourses");

                        for (int i = 0;i < relatedSubCategories.length(); i++){

                            JSONObject subCategoryData = relatedSubCategories.getJSONObject(i);

                            SubCategory subCategory = new SubCategory(subCategoryData.getString("id"), subCategoryData.getString("name"));
                            relatedSubCategoriesList.add(subCategory);
                        }

                        String host = "http://104.131.71.64";
                        for (int i = 0;i < subCategoryCourses.length(); i++){

                            JSONObject relatedCourse = subCategoryCourses.getJSONObject(i);

                            Course course = new Course();
                            course.setServerId(relatedCourse.getString("course_id"));
                            course.setTitle(relatedCourse.getString("course_title"));
                            course.setImagePath(host + relatedCourse.getString("course_img"));
                            course.setInstructor(relatedCourse.getString("course_instructor"));
                            course.setInstructorID(relatedCourse.getString("course_instructor_id"));
                            course.setViewers(relatedCourse.getString("course_viewers"));
                            course.setSave(relatedCourse.getString("course_save"));
                            course.setDuration(relatedCourse.getString("course_duration"));

                            matchedCourseSubCategory.add(course);
                        }

                        relatedSubCategoryAdapter.refreshAdapter(relatedSubCategoriesList);
                        matchedCourseSubCategoryAdapter.notifyDataSetChanged();
                        mCategoryCourseHeading.setText(getResources().getString(
                                R.string.courses_matched_sub_category_view, String.valueOf(subCategoryCourses.length()), "\"" + subCategoryName + "\""));

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
