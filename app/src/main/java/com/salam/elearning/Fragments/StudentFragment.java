package com.salam.elearning.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.salam.elearning.Adapters.CourseAdapter;
import com.salam.elearning.Adapters.SkillAdapter;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.Models.User;
import com.salam.elearning.ProfileActivity;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Student";

    private Context context;
    private View fragmentView;


    private Toolbar toolbar;
    private TextView toolbarTitle;
    private CircleImageView toolbarImage;
    private ImageView toolbarBack;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private User user;
    private String userID;

    private CircleImageView mProfileImage;
    private TextView mProfileUsername;
    private TextView mProfileDesignationCompany;

    private TextView mStudentAddMoreSkill;

    private ArrayList<Skill> allSkills;
    private RecyclerView mStudentSkillsRecyclerView;
    private SkillAdapter mStudentRecylcerAdpter;

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_student, container, false);

        context = getActivity();
        userID = getArguments().getString("userID");
        mSwipeRefreshLayout = fragmentView.findViewById(R.id.swipeRefreshLayout);

        toolbar = fragmentView.findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.profile_username_toolbar);
        toolbarBack = toolbar.findViewById(R.id.profile_back);
        toolbarImage = toolbar.findViewById(R.id.toolbar_profile);
        toolbarBack.setOnClickListener(this);

        mProfileImage = fragmentView.findViewById(R.id.student_profile_image);
        mProfileUsername = fragmentView.findViewById(R.id.student_username);
        mProfileDesignationCompany = fragmentView.findViewById(R.id.student_designation_company);

        mStudentAddMoreSkill = fragmentView.findViewById(R.id.student_add_more_skills);
        mStudentAddMoreSkill.setOnClickListener(this);

        allSkills = new ArrayList<>();
        mStudentSkillsRecyclerView = fragmentView.findViewById(R.id.student_skill_recycler_view);
        mStudentRecylcerAdpter = new SkillAdapter(context , allSkills, R.layout.cell_skill, userID, fragmentView);
        mStudentSkillsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mStudentSkillsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mStudentSkillsRecyclerView.addItemDecoration(dividerItemDecoration);
        mStudentSkillsRecyclerView.setAdapter(mStudentRecylcerAdpter);
        mStudentSkillsRecyclerView.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new GetStudent(userID).execute();

                // Load complete
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new GetStudent(userID).execute();

        return fragmentView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.profile_back:

                getFragmentManager().popBackStack();

                break;

            case R.id.student_add_more_skills:

                Bundle args = new Bundle();
                args.putString("userID", userID);

                AddSkillFragment addSkillFragment = new AddSkillFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0);
                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), addSkillFragment);
                fragmentTransaction.addToBackStack(null);
                addSkillFragment.setArguments(args);
                fragmentTransaction.commit();
                break;

        }
    }

    private class GetStudent extends AsyncTask<Void, Void, String> {

        String userID;
        GetStudent(String userID) {
            this.userID = userID;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);

            NetworkConnection networkConnection = new NetworkConnection();
            String loginApi = "http://104.131.71.64/admin/api/getstudent";
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
                        allSkills = new ArrayList<>();

                        JSONObject data = jsonObject.getJSONObject("response");

                        String imageHost = "http://104.131.71.64";

                        JSONObject user = data.getJSONObject("user");
                        JSONArray skills = data.getJSONArray("skills");

                        Picasso.with(context)
                                .load(Uri.parse(imageHost + user.getString("profile_pic")))
                                .placeholder(R.drawable.cover_placeholder)
                                .error(R.drawable.cover_error)
                                .into(mProfileImage);

                        String username = user.getString("username");
                        String designation = user.getString("designation");
                        String company = user.getString("company");
                        String designationCompany = "";

                        if(!designation.isEmpty()&& !company.isEmpty())
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

                        for (int i = 0; i < skills.length(); i++){
                            JSONObject skillData = skills.getJSONObject(i);

                            allSkills.add(new Skill(skillData.getString("id"), skillData.getString("name")));
                        }

                        mStudentRecylcerAdpter.refreshAdapter(allSkills);

                    } else {

                        Utils.showSnackBar(fragmentView, error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(fragmentView, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(fragmentView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

}
