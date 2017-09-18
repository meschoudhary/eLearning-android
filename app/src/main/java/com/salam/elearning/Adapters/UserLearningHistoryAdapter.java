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


public class UserLearningHistoryAdapter extends RecyclerView.Adapter<UserLearningHistoryAdapter.Holder> {

    private Context context;
    private String userID;
    private int layout;
    private View view;

    public UserLearningHistoryAdapter(Context context, List<Skill> skills, int layout, String userID, View view) {
        this.context = context;
        this.layout = layout;
        this.userID = userID;
        this.view = view;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
            if (itemView.findViewById(R.id.student_delete_skill) != null) {
                mStudentDeleteSkill = itemView.findViewById(R.id.student_delete_skill);
            }

            if (itemView.findViewById(R.id.student_add_skill) != null) {
                mStudentAddSkill = itemView.findViewById(R.id.student_add_skill);
            }

        }
    }
}

