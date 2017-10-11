package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salam.elearning.CourseActivity;
import com.salam.elearning.Models.Course;
import com.salam.elearning.ProfileActivity;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.Holder> {

    private static final String TAG = "CourseAdapter";
    private List<Course> courses;
    private Context context;
    private int layout;
    private View view;
    private String userID;

    public CourseAdapter(Context context, List<Course> courses, String userID, int layout, View view) {
        this.courses = courses;
        this.context = context;
        this.layout = layout;
        this.view = view;
        this.userID = userID;
    }

    public void refreshAdapter(List<Course> items) {
        this.courses = items;
        notifyDataSetChanged();
    }

    public void refreshSavedCourse(int drawable, int position, String save){
        courses.get(position).setSave(save);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Course course = courses.get(position);

        if(holder.courseSkill != null)
            holder.courseSkill.setText(context.getString(R.string.course_cell_skill, course.getSkill()));

        holder.courseCover.setImageURI(Uri.parse(course.getImagePath()));
        holder.courseTitle.setText(course.getTitle());
        holder.courseInstructor.setText(context.getString(R.string.course_cell_instructor, course.getInstructor()));

        holder.courseInstructorID.setText(course.getInstructorID());

        if(holder.courseViewers != null)
            holder.courseViewers.setText(context.getString(R.string.course_cell_viewers, course.getViewers()));

        if(holder.courseScore != null && course.getScore() != null && !course.getScore().isEmpty()){
            holder.courseScore.setText(context.getString(R.string.course_cell_score, course.getScore()));
        }

        int drawable = (course.getSave().equalsIgnoreCase("1")) ? R.drawable.saved_course : R.drawable.save_course;

        Picasso.with(context)
                .load(drawable)
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(holder.courseSave);

        holder.courseSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveCourse(userID, course.getSave(), course.getServerId(), holder.getAdapterPosition()).execute();
            }
        });

        Picasso.with(context)
                .load(Uri.parse(course.getImagePath()))
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(holder.courseCover);

        holder.courseInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userID", course.getInstructorID());
                context.startActivity(intent);
            }
        });

        holder.mCourseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("courseID", course.getServerId());
                intent.putExtra("userID", userID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        LinearLayout mCourseContainer;
        TextView courseSkill;
        ImageView courseCover;
        TextView courseViewers;
        TextView courseTitle;
        TextView courseInstructor;
        ImageView courseSave;
        TextView courseInstructorID;
        TextView courseScore;

        Holder(View itemView) {
            super(itemView);

            mCourseContainer = itemView.findViewById(R.id.course_container);
            courseSkill = itemView.findViewById(R.id.course_skill);
            courseCover = itemView.findViewById(R.id.course_cover);
            courseViewers = itemView.findViewById(R.id.viewers);
            courseTitle = itemView.findViewById(R.id.course_title);
            courseInstructor = itemView.findViewById(R.id.instructor);
            courseSave = itemView.findViewById(R.id.course_save);
            courseInstructorID = itemView.findViewById(R.id.instructor_id);
            courseScore = itemView.findViewById(R.id.course_score);
        }
    }

    private class SaveCourse extends AsyncTask<Void, Void, String> {

        String userID;
        String saved;
        String courseID;
        int position;

        SaveCourse(String userID, String saved, String courseID, int position) {
            this.userID = userID;
            this.courseID = courseID;
            this.saved = (saved.equalsIgnoreCase("1")) ? "0" : "1";
            this.position = position;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("courseID", courseID);
            params.put("save", saved);

            NetworkConnection networkConnection = new NetworkConnection();
            String saveCourseAPI = context.getString(R.string.api_save_course);
            response = networkConnection.performPostCall(saveCourseAPI, params);

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

                        int drawable = (saved.equalsIgnoreCase("1")) ? R.drawable.save_course : R.drawable.saved_course;

                        refreshSavedCourse(drawable, position, saved);

                        String responseSaved = jsonObject.getString("response");

                        Utils.showSnackBar(view, responseSaved, Snackbar.LENGTH_SHORT);

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
