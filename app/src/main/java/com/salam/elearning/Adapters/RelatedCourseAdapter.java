package com.salam.elearning.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.salam.elearning.Models.Course;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RelatedCourseAdapter extends RecyclerView.Adapter<RelatedCourseAdapter.Holder> {

    private Context context;
    private ArrayList<Course> relatedCourseList;

    public RelatedCourseAdapter(Context context, ArrayList<Course> relatedCourseList) {
        this.context = context;
        this.relatedCourseList = relatedCourseList;
    }

    public void refreshAdapter(ArrayList<Course> relatedCourseList){
        this.relatedCourseList = relatedCourseList;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_related_course, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Course course = relatedCourseList.get(position);

        Picasso.with(context)
                .load(course.getImagePath())
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(holder.mCourseImage);

        holder.mCourseTitle.setText(course.getTitle());
        holder.mCourseDuration.setText(course.getDuration());

    }

    @Override
    public int getItemCount() {
        return relatedCourseList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        ImageView mCourseImage;
        TextView mCourseTitle;
        TextView mCourseDuration;

        Holder(View itemView) {
            super(itemView);

            mCourseImage = itemView.findViewById(R.id.related_course_image);
            mCourseTitle = itemView.findViewById(R.id.related_course_title);
            mCourseDuration = itemView.findViewById(R.id.related_course_duration);

        }
    }
}
