package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salam.elearning.Fragments.StudentFragment;
import com.salam.elearning.Models.Course;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.ProfileActivity;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.Holder> {

    private static final String TAG = "DeleteSkill";
    private List<Skill> skills;
    private ArrayList<Skill> tempList = new ArrayList<>();
    private Context context;
    private String userID;
    private int layout;
    private View view;

    public SkillAdapter(Context context, List<Skill> skills, int layout, String userID, View view) {
        this.skills = skills;
        this.context = context;
        this.layout = layout;
        this.userID = userID;
        this.view = view;
        this.tempList.addAll(skills);
    }

    public void refreshAdapter(List<Skill> skills) {
        this.skills = skills;
        notifyDataSetChanged();
    }

    private void removeItem(int position){
        skills.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, skills.size());
    }

    // Filter Class
    public void filter(String charText, List<Skill> skills) {
        charText = charText.toLowerCase(Locale.getDefault());
        tempList.clear();
        if (charText.length() == 0) {
            tempList.addAll(skills);
            this.refreshAdapter(skills);
        } else {
            for (Skill skill : skills) {
                if (skill.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    tempList.add(skill);
                }
            }
        }
        this.refreshAdapter(tempList);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final Skill skill = skills.get(position);

        holder.mStudentSkill.setText(skill.getName());
        if(holder.mStudentDeleteSkill != null){
            holder.mStudentDeleteSkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DeleteSkill(userID, skill.getServerId(), holder.getAdapterPosition()).execute();
                }
            });
        }

        if(holder.mStudentAddSkill != null){
            holder.mStudentAddSkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AddSkill(userID, skill.getServerId(), holder.getAdapterPosition()).execute();
                }
            });
        }


        holder.mStudentSkillCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You want to view skill " + skill.getName() + "with id " + skill.getServerId(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        RelativeLayout mStudentSkillCell;
        TextView mStudentSkill;
        ImageView mStudentDeleteSkill;
        ImageView mStudentAddSkill;

        Holder(View itemView) {
            super(itemView);

            mStudentSkillCell = itemView.findViewById(R.id.student_skill_cell);
            mStudentSkill = itemView.findViewById(R.id.student_skill);
            if(itemView.findViewById(R.id.student_delete_skill) != null){
                mStudentDeleteSkill = itemView.findViewById(R.id.student_delete_skill);
            }

            if(itemView.findViewById(R.id.student_add_skill) != null){
                mStudentAddSkill = itemView.findViewById(R.id.student_add_skill);
            }

        }
    }

    private class DeleteSkill extends AsyncTask<Void, Void, String> {

        String userID;
        String skillID;
        int position;

        private DeleteSkill(String userID, String skillID, int position) {
            this.userID = userID;
            this.skillID = skillID;
            this.position = position;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("skillID", skillID);

            NetworkConnection networkConnection = new NetworkConnection();
            String loginApi = "http://104.131.71.64/admin/api/deleteskill";
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
                        SkillAdapter.this.removeItem(position);

                        Utils.showSnackBar(view, "Skill deleted", Snackbar.LENGTH_SHORT);
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

    private class AddSkill extends AsyncTask<Void, Void, String> {

        String userID;
        String skillID;
        int position;

        private AddSkill(String userID, String skillID, int position) {
            this.userID = userID;
            this.skillID = skillID;
            this.position = position;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("skillID", skillID);

            NetworkConnection networkConnection = new NetworkConnection();
            String loginApi = "http://104.131.71.64/admin/api/addskill";
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
                        SkillAdapter.this.removeItem(position);
                        Utils.showSnackBar(view, "Skill added", Snackbar.LENGTH_SHORT);
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
