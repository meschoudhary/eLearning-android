package com.salam.elearning.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.halilibo.bettervideoplayer.BetterVideoProgressCallback;
import com.halilibo.bettervideoplayer.subtitle.CaptionsView;
import com.salam.elearning.CourseActivity;
import com.salam.elearning.Fragments.CourseContentFragment;
import com.salam.elearning.Models.CourseChapters;
import com.salam.elearning.Models.CourseSections;
import com.salam.elearning.R;
import com.salam.elearning.Utils.DownloadTask;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class CourseSectionAdapter extends RecyclerView.Adapter<CourseSectionAdapter.Holder>{

    private BetterVideoPlayer videoView;
    private Context context;
    private ArrayList<CourseSections> courseSectionsList;
    private View view;

    public CourseSectionAdapter(Context context, BetterVideoPlayer videoView, ArrayList<CourseSections> courseSectionsList, View view) {
        this.videoView = videoView;
        this.context = context;
        this.courseSectionsList = courseSectionsList;
        this.view = view;
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_section, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        CourseSections courseSections = courseSectionsList.get(position);
        holder.onBind(courseSections, context);

    }

    @Override
    public int getItemCount() {
        return courseSectionsList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        private RelativeLayout mSectionCellContainer;
        private TextView sectionName;
        private ImageView arrow;
        private RecyclerView mSectionContentRecyclerView;
        private List<Object> mSectionContentList;
        private SectionContentAdapter sectionContentAdapter;

        Holder(View itemView) {
            super(itemView);

            sectionName = itemView.findViewById(R.id.course_section);
            mSectionContentRecyclerView = itemView.findViewById(R.id.section_content);
            arrow = itemView.findViewById(R.id.arrow);
            mSectionCellContainer = itemView.findViewById(R.id.section_cell_container);
        }

        void onBind(CourseSections courseSections, Context context){

            mSectionContentList = courseSections.getSectionContent();
            sectionName.setText(courseSections.getName());
            mSectionCellContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mSectionContentRecyclerView.getVisibility() == View.GONE){
                        mSectionContentRecyclerView.setVisibility(View.VISIBLE);
                    }else {
                        mSectionContentRecyclerView.setVisibility(View.GONE);
                    }

                }
            });

            sectionContentAdapter = new SectionContentAdapter(context,  mSectionContentList, videoView, courseSections, view);

            mSectionContentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mSectionContentRecyclerView.setAdapter(sectionContentAdapter);
        }
    }
}


