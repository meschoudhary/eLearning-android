package com.salam.elearning;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salam.elearning.Adapters.SearchSuggestionAdaptor;
import com.salam.elearning.Adapters.SearchedCourseAdapter;
import com.salam.elearning.Fragments.FilterFragment;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.SearchSuggestion;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.TypefaceSpan;
import com.salam.elearning.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements FilterFragment.OnDataPass {

    private static final String TAG = "Searching";
    private User user;
    private String query = "";

    public SearchView searchView;
    SearchSuggestionAdaptor mSearchViewAdapter;
    public static String[] columns = new String[]{"_id", "SERVERID","IMAGE", "TITLE", "TYPE", "TYPEID"};
    private ArrayList<SearchSuggestion> searchSuggestionArrayList = new ArrayList<>();

    private ArrayList<Course> courseList = new ArrayList<>();
    private RecyclerView searchedCoursesRecyclerView;
    private SearchedCourseAdapter searchedCoursesAdapter;

    private ProgressBar progressBar;

    private FilterFragment filterFragment;

    private String levelValue = "All";
    private String typeValue = "all";
    private String durationValue = "0";

    private RelativeLayout mNoSearchResultsView;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        List<User> users = User.getLoggedInUser();
        user = users.get(0);

        mNoSearchResultsView = findViewById(R.id.no_results_view);

        progressBar = findViewById(R.id.progress_bar);
        courseList = new ArrayList<>();
        searchedCoursesAdapter = new SearchedCourseAdapter(this, courseList, user.getServerId(), R.layout.cell_searched_course, findViewById(R.id.searched_courses_view));
        searchedCoursesRecyclerView = findViewById(R.id.searched_courses_recycler_view);
        searchedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchedCoursesRecyclerView.addItemDecoration(new DividerItemDecoration(searchedCoursesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        searchedCoursesRecyclerView.setAdapter(searchedCoursesAdapter);

        filterFragment = new FilterFragment();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.done).setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchViewAdapter = new SearchSuggestionAdaptor(this, R.layout.cell_search_suggestion, null, columns, null, -1000, user.getServerId());
        searchView.setSuggestionsAdapter(mSearchViewAdapter);
        searchView.setQuery(query, false);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                Intent intent;
                SearchSuggestion searchSuggestion = searchSuggestionArrayList.get(position);
                String val = searchSuggestion.getTitle();

                if(searchSuggestion.getTypeID().isEmpty()){
                    intent = new Intent(SearchActivity.this, CourseActivity.class);
                    intent.putExtra("courseID", searchSuggestion.getServerID());
                    intent.putExtra("userID", user.getServerId());
                    startActivity(intent);
                }else {
                    intent = new Intent(SearchActivity.this, SearchActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchManager.QUERY, val);
                    startActivity(intent);
                }

                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, query);

                if (query.length() > 2) {
                    loadData(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                Log.d(TAG, query);

                if (query.length() > 2) {
                    loadData(query);
                }

                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:

                user.setLoggedIn("0");
                user.save();

                Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                startActivity(intent);

                return true;

            case R.id.search:
                return super.onOptionsItemSelected(item);

            case R.id.filter:

                //if(courseList.size() > 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    if (filterFragment.isVisible())
                        return true;
                    else {

                        Bundle args = new Bundle();
                        args.putString("query", query);
                        args.putString("level", levelValue);
                        args.putString("type", typeValue);
                        args.putString("duration", durationValue);

                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0);
                        fragmentTransaction.replace(R.id.searched_courses_view, filterFragment);
                        filterFragment.setArguments(args);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        return true;
                    }
               // }else {
                //    Utils.showSnackBar(findViewById(R.id.searched_courses_view), "Can not filter results now.", Snackbar.LENGTH_SHORT);
                //    return true;
               // }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleIntent(Intent intent) {


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            query = intent.getStringExtra(SearchManager.QUERY);

            if(query.length() > 0){

                SpannableString actionBarTitle = new SpannableString("You searched for : " + query);
                actionBarTitle.setSpan(new TypefaceSpan(this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                getSupportActionBar().setTitle(actionBarTitle);

                progressBar.setVisibility(View.VISIBLE);
                new GetSearchData(query, levelValue, typeValue, durationValue).execute();
            }


        }
    }

    private void loadData(String searchText) {

        searchSuggestionArrayList = new ArrayList<>();
        new GetSearchSuggestion(searchText).execute();
    }

    @Override
    public void onDataPass(String level, String type, String duration) {
        levelValue = level;
        typeValue = type;
        durationValue = duration;

        new GetSearchData(query, levelValue, typeValue, durationValue).execute();
    }

    private class GetSearchSuggestion extends AsyncTask<Void, Void, String> {

        String query;

        GetSearchSuggestion(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("value", query);

            NetworkConnection networkConnection = new NetworkConnection();
            String forgetPasswordApi = getString(R.string.api_search);
            response = networkConnection.performPostCall(forgetPasswordApi, params);

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
                        Log.d(TAG, data.toString());


                        JSONArray skillData = data.getJSONArray("skill");
                        for (int i = 0; i < skillData.length(); i++){

                            String skill = skillData.getString(i);

                            SearchSuggestion searchSuggestion = new SearchSuggestion("", "", skill, "Skill", "1");
                            searchSuggestionArrayList.add(searchSuggestion);
                        }




                        JSONArray categoryData = data.getJSONArray("categories");
                        for (int i = 0; i < categoryData.length(); i++){

                            String category = categoryData.getString(i);

                            SearchSuggestion searchSuggestion = new SearchSuggestion("", "", category, "Category", "1");
                            searchSuggestionArrayList.add(searchSuggestion);
                        }





                        JSONArray subCategoryData = data.getJSONArray("subCategories");
                        for (int i = 0; i < subCategoryData.length(); i++){

                            String subCategory = subCategoryData.getString(i);

                            SearchSuggestion searchSuggestion = new SearchSuggestion("", "", subCategory, "Sub Category", "1");
                            searchSuggestionArrayList.add(searchSuggestion);
                        }



                        JSONArray courseData = data.getJSONArray("course");
                        for (int i = 0; i < courseData.length(); i++){

                            JSONObject course = courseData.getJSONObject(i);

                            SearchSuggestion searchSuggestion = new SearchSuggestion(course.getString("id"), course.getString("img"),
                                    course.getString("title"), "Course", "");
                            searchSuggestionArrayList.add(searchSuggestion);
                        }





                        JSONArray chapterData = data.getJSONArray("chapter");

                        for (int i = 0; i < chapterData.length(); i++){

                            JSONObject chapter = chapterData.getJSONObject(i);

                            SearchSuggestion searchSuggestion = new SearchSuggestion(
                                    chapter.getString("course_id"),
                                    chapter.getString("img"),
                                    chapter.getString("title"), "Chapter", "");
                            searchSuggestionArrayList.add(searchSuggestion);
                        }



                        MatrixCursor cursor = new MatrixCursor(columns);
                        int i = 0;
                        for (SearchSuggestion searchSuggestion : searchSuggestionArrayList) {
                            String[] temp = new String[6];
                            i = i + 1;

                            temp[0] = Integer.toString(i);
                            temp[1] = searchSuggestion.getServerID();
                            temp[2] = searchSuggestion.getImage();
                            temp[3] = searchSuggestion.getTitle();
                            temp[4] = searchSuggestion.getType();
                            temp[5] = searchSuggestion.getTypeID();

                            cursor.addRow(temp);
                        }
                        mSearchViewAdapter.changeCursor(cursor);
                        mSearchViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "everything should be done");

                    } else {

                        Utils.showSnackBar(findViewById(R.id.main_activity_screen), error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(findViewById(R.id.main_activity_screen), e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(findViewById(R.id.main_activity_screen), "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private class GetSearchData extends AsyncTask<Void, Void, String> {

        String query;
        String level;
        String type;
        String duration;

        GetSearchData(String query, String level, String type, String duration) {
            this.query = query;
            this.level = level;
            this.type = type;
            this.duration = duration;
        }

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", user.getServerId());
            params.put("val", query);
            params.put("level", level);
            params.put("type", type);
            params.put("duration", duration);

            NetworkConnection networkConnection = new NetworkConnection();
            String filterApi = getString(R.string.api_filter);
            response = networkConnection.performPostCall(filterApi, params);

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.isEmpty()){

                try {

                    courseList = new ArrayList<>();
                    Log.d(TAG, response);

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        JSONArray data = jsonObject.getJSONArray("response");

                        if(data.length() > 0){
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject courseData = data.getJSONObject(i);

                                String id = courseData.getString("id");
                                String title = courseData.getString("title");
                                String type = courseData.getString("type");
                                String img = courseData.getString("img");
                                String instructor = courseData.getString("instructor");
                                String duration = courseData.getString("duration");
                                String instructorID = courseData.getString("instructorID");
                                String saved = courseData.getString("saved");

                                Course course = new Course(title, img, instructor, id, "", "", "",instructorID);
                                course.setType(type);
                                course.setDuration(duration);
                                course.setSave(saved);
                                courseList.add(course);
                            }

                            searchedCoursesAdapter.refreshAdapter(courseList);
                            mNoSearchResultsView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, data.toString());

                        }else{
                            mNoSearchResultsView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        }
                        Log.d(TAG, "everything should be done");

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Utils.showSnackBar(findViewById(R.id.searched_courses_view), error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Utils.showSnackBar(findViewById(R.id.searched_courses_view), e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                progressBar.setVisibility(View.GONE);
                Utils.showSnackBar(findViewById(R.id.searched_courses_view), "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }
}
