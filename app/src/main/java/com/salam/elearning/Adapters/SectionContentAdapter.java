package com.salam.elearning.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.salam.elearning.CourseActivity;
import com.salam.elearning.Fragments.CategoryFragment;
import com.salam.elearning.Fragments.HomeFragment;
import com.salam.elearning.Fragments.ProfileFragment;
import com.salam.elearning.Fragments.QuizFragment;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.CourseChapters;
import com.salam.elearning.Models.CourseQuiz;
import com.salam.elearning.Models.CourseSections;
import com.salam.elearning.Models.Quiz;
import com.salam.elearning.QuizActivity;
import com.salam.elearning.R;
import com.salam.elearning.Utils.DownloadTask;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

public class SectionContentAdapter extends RecyclerView.Adapter<SectionContentAdapter.ContentHolder> {

    private Context context;
    private List<Object> sectionContentList;
    private BetterVideoPlayer videoView;
    private CourseSections courseSection;
    private View view;

    SectionContentAdapter(Context context, List<Object> sectionContentList, BetterVideoPlayer videoView, CourseSections courseSection, View view) {
        this.context = context;
        this.sectionContentList = sectionContentList;
        this.videoView = videoView;
        this.courseSection = courseSection;
        this.view = view;
    }

    @Override
    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_section_content, parent, false);
        return new ContentHolder(v);
    }

    @Override
    public void onBindViewHolder(ContentHolder holder, int position) {

        Object object = sectionContentList.get(position);

        holder.onBind(context, object, videoView, courseSection);
    }

    @Override
    public int getItemCount() {
        return sectionContentList.size();
    }

    class ContentHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mContainer;
        private ImageView mCompleted;
        private TextView mTitle;
        private TextView mSubTextView;
        private ImageView mDownload;

        ContentHolder(View itemView) {
            super(itemView);

            mCompleted = itemView.findViewById(R.id.chapter_completed);
            mTitle = itemView.findViewById(R.id.chapter_name);
            mSubTextView = itemView.findViewById(R.id.chapter_duration);
            mDownload = itemView.findViewById(R.id.chapter_download);
            mContainer = itemView.findViewById(R.id.chapter_container);

        }

        void onBind(final Context context, Object sectionContentObject, final BetterVideoPlayer videoView, CourseSections courseSection){

            if (sectionContentObject instanceof CourseChapters){

                CourseChapters courseChapters = ((CourseChapters) sectionContentObject);

                final String title = courseChapters.getName();
                String duration = courseChapters.getDuration();
                final String videoPath = courseChapters.getVideo();
                String transcriptPath = courseChapters.getTranscript();
                boolean completed = courseChapters.isCompleted();

                mTitle.setText(title);
                mSubTextView.setText(duration);

                if(completed){
                    mCompleted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.course_complete));
                }

                Picasso.with(context)
                        .load(R.drawable.download)
                        .placeholder(R.drawable.cover_placeholder)
                        .error(R.drawable.cover_error)
                        .into(mDownload);

                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        videoView.reset();
                        videoView.setSource(Uri.parse(videoPath));
                    }
                });

                mDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DownloadTask(context, videoPath, title, view);
                    }
                });

            }else if(sectionContentObject instanceof CourseQuiz){

                final CourseQuiz courseQuiz = ((CourseQuiz) sectionContentObject);

                String title = "Quiz";
                String subTextView = "";
                if(courseQuiz.getQuizCount() > 1){
                    subTextView = courseQuiz.getQuizCount() + " Quizes";
                }else{
                    subTextView = courseQuiz.getQuizCount() + " Quiz";
                }

                mTitle.setText(title);
                mSubTextView.setText(subTextView);
                mDownload.setVisibility(View.GONE);

                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_quiz);

                        Button startQuiz = dialog.findViewById(R.id.start_quiz);

                        startQuiz.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("quiz", (Serializable) courseQuiz.getQuizList());
                                Intent intent = new Intent(context, QuizActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                });

            }
        }
    }
}
