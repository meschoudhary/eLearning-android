package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.salam.elearning.Models.Course;
import com.salam.elearning.ProfileActivity;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.Holder> {

    private List<Course> courses;
    private Context context;
    private int layout;

    public CourseAdapter(Context context, List<Course> courses, int layout) {
        this.courses = courses;
        this.context = context;
        this.layout = layout;
    }

    public void refreshAdapter(List<Course> items) {
        this.courses = items;
        notifyDataSetChanged();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Course course = courses.get(position);

        if(holder.courseSkill != null)
            holder.courseSkill.setText(context.getString(R.string.course_cell_skill, course.getSkill()));

        holder.courseCover.setImageURI(Uri.parse(course.getImagePath()));
        holder.courseTitle.setText(course.getTitle());
        holder.courseInstructor.setText(context.getString(R.string.course_cell_instructor, course.getInstructor()));

        holder.courseInstructorID.setText(course.getInstructorID());

        if(holder.courseViewers != null)
            holder.courseViewers.setText(context.getString(R.string.course_cell_viewers, course.getViewers()));
        if(course.getSave().equalsIgnoreCase("1")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.courseSave.setImageDrawable(context.getDrawable(R.drawable.saved_course));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.courseSave.setImageDrawable(context.getDrawable(R.drawable.save_course));
            }
        }

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
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView courseSkill;
        ImageView courseCover;
        TextView courseViewers;
        TextView courseTitle;
        TextView courseInstructor;
        ImageView courseSave;
        TextView courseInstructorID;

        Holder(View itemView) {
            super(itemView);

            courseSkill = itemView.findViewById(R.id.course_skill);
            courseCover = itemView.findViewById(R.id.course_cover);
            courseViewers = itemView.findViewById(R.id.viewers);
            courseTitle = itemView.findViewById(R.id.course_title);
            courseInstructor = itemView.findViewById(R.id.instructor);
            courseSave = itemView.findViewById(R.id.course_save);
            courseInstructorID = itemView.findViewById(R.id.instructor_id);
        }
    }
}
