package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salam.elearning.Models.Collection;
import com.salam.elearning.Models.Course;
import com.salam.elearning.R;

import java.util.ArrayList;
import java.util.HashMap;


public class UserLearningHistoryAdapter extends RecyclerView.Adapter<UserLearningHistoryAdapter.Holder> {

    private Context context;
    private String userID;
    private View view;
    private ArrayList<Object> allData;

    private HashMap<String, ArrayList<Course>> collectionCourses;

    private ArrayList dataForAdapter;

    private CourseAdapter mCourseAdapter;
    private CollectionAdapter mCollectionAdapter;

    public UserLearningHistoryAdapter(Context context, ArrayList<Object> allData,
                                      HashMap<String, ArrayList<Course>> collectionCourses,
                                      String userID, View view) {
        this.context = context;
        this.allData = allData;
        this.collectionCourses = collectionCourses;
        this.userID = userID;
        this.view = view;
    }

    public void refreshAdapter(ArrayList<Object> allData, HashMap<String, ArrayList<Course>> collectionCourses) {
        this.allData = allData;
        this.collectionCourses = collectionCourses;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_learning_history, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Resources res = context.getResources();
        switch (position){

            case 0:

                dataForAdapter = (ArrayList<Course>) allData.get(position);

                holder.mContentHeading.setText(context.getString(R.string.content_saved_heading_saved, "" + dataForAdapter.size()));

                mCourseAdapter = new CourseAdapter(context, dataForAdapter, userID, R.layout.cell_saved_course, view);
                holder.mContentCourses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.mContentCourses.setAdapter(mCourseAdapter);

                break;

            case 1:

                dataForAdapter = (ArrayList<Course>) allData.get(position);

                holder.mContentHeading.setText(res.getString(R.string.content_saved_heading_history, "" + dataForAdapter.size()));

                mCourseAdapter = new CourseAdapter(context, dataForAdapter, userID, R.layout.cell_learning_history_course, view);
                holder.mContentCourses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.mContentCourses.setAdapter(mCourseAdapter);
                break;

            case 2:

                dataForAdapter = (ArrayList<Collection>) allData.get(position);

                holder.mContentHeading.setText(res.getString(R.string.content_saved_heading_collections, "" + dataForAdapter.size()));

                mCollectionAdapter = new CollectionAdapter(context, dataForAdapter, collectionCourses);
                holder.mContentCourses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.mContentCourses.setAdapter(mCollectionAdapter);

                break;
        }

    }

    @Override
    public int getItemCount() {
        return allData.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView mContentHeading;
        TextView mContentSeeAll;

        RecyclerView mContentCourses;

        Holder(View itemView) {
            super(itemView);

            mContentHeading = itemView.findViewById(R.id.content_heading);
            mContentSeeAll = itemView.findViewById(R.id.content_see_all);

            mContentCourses = itemView.findViewById(R.id.content_courses);
        }
    }
}

