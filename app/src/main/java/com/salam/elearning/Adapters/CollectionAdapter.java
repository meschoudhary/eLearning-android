package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salam.elearning.CollectionActivity;
import com.salam.elearning.Models.Collection;
import com.salam.elearning.Models.Course;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.Holder> {

    private Context context;
    private ArrayList collectionData;
    private HashMap<String, ArrayList<Course>> collectionCourse;

    private ArrayList<Course> collectionCourseList;

    public CollectionAdapter(Context context, ArrayList collectionData, HashMap<String, ArrayList<Course>> collectionCourse) {
        this.context = context;
        this.collectionData = collectionData;
        this.collectionCourse = collectionCourse;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_collection_course, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        final Collection collection = (Collection) collectionData.get(position);

        collectionCourseList = new ArrayList<>();
        collectionCourseList.addAll(collectionCourse.get(collection.getServerID()));

        int collectionCourseListSize = collectionCourseList.size();

        holder.mCollectionName.setText(collection.getName());
        ;
        holder.mCollectionCourseCount.setText(context.getResources().getString(R.string.content_saved_heading_collections_count, collectionCourseListSize + ""));

        for (int i = 0; i < collectionCourseListSize; i++){
            Uri imagePath = Uri.parse(collectionCourseList.get(i).getImagePath());

            switch (i){

                case 0:

                    Picasso.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.cover_placeholder)
                            .error(R.drawable.cover_error)
                            .into(holder.mCourseImage1);

                    break;

                case 1:

                    Picasso.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.cover_placeholder)
                            .error(R.drawable.cover_error)
                            .into(holder.mCourseImage2);
                    break;

                case 2:

                    Picasso.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.cover_placeholder)
                            .error(R.drawable.cover_error)
                            .into(holder.mCourseImage3);
                    break;

                case 3:

                    Picasso.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.cover_placeholder)
                            .error(R.drawable.cover_error)
                            .into(holder.mCourseImage4);
                    break;
            }
        }

        holder.mCollectionCoursesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putSerializable("collection", collection);
                bundle.putSerializable("collectionData", collectionCourseList);

                Intent intent = new Intent(context, CollectionActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return collectionData.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        ImageView mCourseImage1;
        ImageView mCourseImage2;
        ImageView mCourseImage3;
        ImageView mCourseImage4;

        TextView mCollectionCourseCount;
        TextView mCollectionName;

        RelativeLayout mCollectionCoursesContainer;

        Holder(View itemView) {
            super(itemView);

            mCollectionCoursesContainer = itemView.findViewById(R.id.collection_courses_container);

            mCollectionCourseCount = itemView.findViewById(R.id.collection_courses_count);
            mCollectionName = itemView.findViewById(R.id.collection_name);

            mCourseImage1 = itemView.findViewById(R.id.collection_course_image_1);
            mCourseImage2 = itemView.findViewById(R.id.collection_course_image_2);
            mCourseImage3 = itemView.findViewById(R.id.collection_course_image_3);
            mCourseImage4 = itemView.findViewById(R.id.collection_course_image_4);
        }
    }
}
