package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salam.elearning.CourseActivity;
import com.salam.elearning.Models.Course;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class CategoryCourseAdapter extends RecyclerView.Adapter<CategoryCourseAdapter.Holder> {

    private Context context;
    private List<Course> courseList;
    private String userID;

    public CategoryCourseAdapter(Context context, List<Course> courseList, String userID) {
        this.context = context;
        this.courseList = courseList;
        this.userID = userID;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_category_course, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Course course = courseList.get(position);

        holder.Bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private RelativeLayout mCategoryCourseContainer;
        private ImageView mImage;
        private TextView mDuration;
        private TextView mSkill;
        private TextView mTitle;
        private ImageView mSaved;

        public Holder(View itemView) {
            super(itemView);

            mCategoryCourseContainer = itemView.findViewById(R.id.category_course_container);
            mImage = itemView.findViewById(R.id.category_view_course_image);
            mDuration = itemView.findViewById(R.id.category_view_course_duration);
            mSkill = itemView.findViewById(R.id.category_view_course_skill);
            mTitle = itemView.findViewById(R.id.category_view_course_title);
            mSaved = itemView.findViewById(R.id.category_view_course_save);
        }

        void Bind(final Course course){

            Picasso.with(context)
                    .load(course.getImagePath())
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_error)
                    .into(mImage);

            mTitle.setText(course.getTitle());
            mDuration.setText(course.getDuration());

            int drawable = (course.getSave().equalsIgnoreCase("1")) ? R.drawable.saved_course : R.drawable.save_course;

            Picasso.with(context)
                    .load(drawable)
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_error)
                    .into(mSaved);

            mCategoryCourseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CourseActivity.class);
                    intent.putExtra("courseID", course.getServerId());
                    intent.putExtra("userID", userID);
                    context.startActivity(intent);
                }
            });
        }
    }
}
