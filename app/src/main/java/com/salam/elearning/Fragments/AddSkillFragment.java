package com.salam.elearning.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.salam.elearning.Adapters.SkillAdapter;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.NetworkUtils;
import com.salam.elearning.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSkillFragment extends Fragment implements SearchView.OnQueryTextListener  {

    private static final String TAG = "AddMoreSkill";
    private Context context;
    private View fragmentView;

    private ProgressBar mProgressBar;
    private SearchView mSearchView;

    private ArrayList<Skill> allSkills;
    private RecyclerView mAllSkillsRecyclerView;
    private SkillAdapter mAllSkillsRecyclerAdapter;

    private String userID;

    public AddSkillFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getActivity();
        fragmentView = inflater.inflate(R.layout.fragment_add_skill, container, false);

        mProgressBar = fragmentView.findViewById(R.id.progress_bar);

        mSearchView = fragmentView.findViewById(R.id.search_skill);
        mSearchView.setOnQueryTextListener(this);

        allSkills = new ArrayList<>();

        userID = getArguments().getString("userID");

        mAllSkillsRecyclerView = fragmentView.findViewById(R.id.all_skills);

        mAllSkillsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mAllSkillsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mAllSkillsRecyclerView.addItemDecoration(dividerItemDecoration);

        mAllSkillsRecyclerAdapter = new SkillAdapter(context , allSkills, R.layout.cell_skill_add, userID, fragmentView);
        mAllSkillsRecyclerView.setAdapter(mAllSkillsRecyclerAdapter);

        new GetAllSkills(userID).execute();

        return fragmentView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAllSkillsRecyclerAdapter.filter(query, allSkills);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchterm) {

        mAllSkillsRecyclerAdapter.filter(searchterm, allSkills);
        return false;
    }

    private class GetAllSkills extends AsyncTask<Void, Void, String> {

        String userID;

        GetAllSkills(String userID) {
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
                String loginApi = context.getString(R.string.api_get_all_skills);
                response = networkConnection.performPostCall(loginApi, params);
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

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        Log.d(TAG, response);

                        JSONArray allSkillsData = jsonObject.getJSONArray("response");

                        for (int i = 0; i < allSkillsData.length(); i++){

                            JSONObject skillData = allSkillsData.getJSONObject(i);

                            allSkills.add(new Skill(skillData.getString("id"), skillData.getString("name")));
                        }

                        mAllSkillsRecyclerAdapter.refreshAdapter(allSkills);

                    } else {
                        Log.d(TAG, error);
                        Utils.showSnackBar(fragmentView, error, Snackbar.LENGTH_SHORT);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showSnackBar(fragmentView, e.getMessage(), Snackbar.LENGTH_SHORT);
                }

            }else{
                Log.d(TAG, response);
                Utils.showSnackBar(fragmentView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
