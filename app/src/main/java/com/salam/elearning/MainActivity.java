package com.salam.elearning;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.LoginFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.salam.elearning.Adapters.SearchSuggestionAdaptor;
import com.salam.elearning.Fragments.CategoryFragment;
import com.salam.elearning.Fragments.HomeFragment;
import com.salam.elearning.Fragments.ProfileFragment;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private User user;

    private AlertDialog alert;
    private AlertDialog alertDialog;
    private SpannableString actionBarTitle;

    private SearchView searchView;

    SearchSuggestionAdaptor mSearchViewAdapter;
    public static String[] columns = new String[]{"_id", "SERVERID","IMAGE", "TITLE", "TYPE", "TYPEID"};
    private ArrayList<SearchSuggestion> searchSuggestionArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = User.getLoggedInUser();
        user = users.get(0);

        actionBarTitle = new SpannableString(getResources().getString(R.string.home_fragment));
        actionBarTitle.setSpan(new TypefaceSpan(MainActivity.this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(actionBarTitle);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.home_fragment, HomeFragment.class)
                .add(R.string.profile_fragment, ProfileFragment.class)
                .add(R.string.category_fragment, CategoryFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        final SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {

            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) LayoutInflater.from(viewPagerTab.getContext()).inflate(R.layout.single_tab,container, false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home_selected));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("scrolled", "scrolled");
                Log.d("position", position + "");
            }
            @Override
            public void onPageSelected(int position) {

                View tab = viewPagerTab.getTabAt(position);
                ImageView icon = tab.findViewById(R.id.tab_icon);

                View homeTab;
                ImageView homeTabIcon;

                View categoryTab;
                ImageView categoryTabIcon;

                View historyTab;
                ImageView historyTabIcon;

                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home_selected));

                        historyTab = viewPagerTab.getTabAt(1);
                        historyTabIcon = historyTab.findViewById(R.id.tab_icon);
                        historyTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));

                        categoryTab = viewPagerTab.getTabAt(2);
                        categoryTabIcon = categoryTab.findViewById(R.id.tab_icon);
                        categoryTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));

                        actionBarTitle = new SpannableString(getResources().getString(R.string.home_fragment));
                        actionBarTitle.setSpan(new TypefaceSpan(MainActivity.this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle(actionBarTitle);

                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history_selected));


                        homeTab = viewPagerTab.getTabAt(0);
                        homeTabIcon = homeTab.findViewById(R.id.tab_icon);
                        homeTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home));

                        categoryTab = viewPagerTab.getTabAt(2);
                        categoryTabIcon = categoryTab.findViewById(R.id.tab_icon);
                        categoryTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));

                        actionBarTitle = new SpannableString(getResources().getString(R.string.profile_fragment));
                        actionBarTitle.setSpan(new TypefaceSpan(MainActivity.this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle(actionBarTitle);

                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category_selected));

                        homeTab = viewPagerTab.getTabAt(0);
                        homeTabIcon = homeTab.findViewById(R.id.tab_icon);
                        homeTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home));

                        historyTab = viewPagerTab.getTabAt(1);
                        historyTabIcon = historyTab.findViewById(R.id.tab_icon);
                        historyTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));

                        actionBarTitle = new SpannableString(getResources().getString(R.string.category_fragment));
                        actionBarTitle.setSpan(new TypefaceSpan(MainActivity.this, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle(actionBarTitle);

                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("scrolled_state_changed", state + "");
            }
        });
        viewPagerTab.setViewPager(viewPager);

        if(user.getPasswordChanged() != null && user.getPasswordChanged().equalsIgnoreCase("1")){

            final EditText mUserUpdatedpassword = new EditText(this);

            alert = new AlertDialog.Builder(this)
                    .setView(mUserUpdatedpassword)
                    .setTitle(R.string.update_password_title)
                    .setMessage(R.string.update_password_message)
                    .setPositiveButton(R.string.update_password_button, null) //Set to null. We override the onclick
                    .create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something

                            String updatedPassword = mUserUpdatedpassword.getText().toString().trim();

                            if(updatedPassword.isEmpty()){

                                mUserUpdatedpassword.requestFocus();
                                mUserUpdatedpassword.setError("Password cannot be empty");

                            }else if(updatedPassword.length() < 6){

                                mUserUpdatedpassword.requestFocus();
                                mUserUpdatedpassword.setError("Password must be atleast 6 characters");

                            }else{

                                new SetPassword(updatedPassword).execute();
                            }
                        }
                    });
                }
            });

            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.filter).setVisible(false);
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
                    intent = new Intent(MainActivity.this, CourseActivity.class);
                    intent.putExtra("courseID", searchSuggestion.getServerID());
                    intent.putExtra("userID", user.getServerId());
                    startActivity(intent);
                }else {
                    intent = new Intent(MainActivity.this, SearchActivity.class);
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

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                return true;

            case R.id.search:
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        } else {
            super.onBackPressed(); //replaced
        }
    }

    private class SetPassword extends AsyncTask<Void, Void, String> {

        String password;

        SetPassword(String password) {
            this.password = password;

        }

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", user.getServerId());
            params.put("password", password);

            NetworkConnection networkConnection = new NetworkConnection();
            String setPasswordApi = getString(R.string.api_set_password);
            response = networkConnection.performPostCall(setPasswordApi, params);

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

                        user.setPasswordChanged("0");
                        user.update();
                        alert.dismiss();
                        String responseData = jsonObject.getString("response");
                        Utils.showSnackBar(findViewById(R.id.main_activity_screen), responseData, Snackbar.LENGTH_SHORT);


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

    private void loadData(String searchText) {

        searchSuggestionArrayList = new ArrayList<>();
        new GetSearchSuggestion(searchText).execute();
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
                    searchSuggestionArrayList = new ArrayList<>();
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

                            SearchSuggestion searchSuggestion = new SearchSuggestion("", "", skill,"Skill", "1");
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

                           /* Course courseModel = new Course(course.getString("title"), course.getString("id"));
                            courseModel.save();*/
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
}
