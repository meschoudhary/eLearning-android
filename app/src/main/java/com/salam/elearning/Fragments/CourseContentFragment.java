package com.salam.elearning.Fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.halilibo.bettervideoplayer.BetterVideoProgressCallback;
import com.halilibo.bettervideoplayer.subtitle.CaptionsView;
import com.salam.elearning.Adapters.CourseSectionAdapter;
import com.salam.elearning.Models.CourseChapters;
import com.salam.elearning.Models.CourseSections;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseContentFragment extends Fragment implements BetterVideoCallback {

    private String TAG = "CourseContentFragment";
    private View fragmentView;

    private RecyclerView courseContentRecyclerView;
    private CourseSectionAdapter adapter;

    private Context context;
    private BetterVideoPlayer mVideoPlayer;

    private String userID;
    private ArrayList<CourseSections> courseContentList;
    private ArrayList<CourseChapters> chaptersList;

    public static String courseID;
    public static String chapterID;

    int lastPosition = 0;

    public CourseContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_course_content, container, false);

        context = getActivity();
        userID  = getArguments().getString("userID");
        courseContentList = (ArrayList<CourseSections>) getArguments().getSerializable("courseContentList");

        String firstVideoPath = "";
        String firstTranscriptPath = "";

        if(courseContentList != null){

            if(courseContentList.get(0).getSectionContent().get(0) instanceof CourseChapters){

                CourseChapters courseChapters = (CourseChapters) courseContentList.get(0).getSectionContent().get(0);
                firstVideoPath = courseChapters.getVideo();
                firstTranscriptPath = courseChapters.getTranscript();
                courseID = courseChapters.getCourseID();
                chapterID = courseChapters.getServerID();
            }else{
                Log.d(TAG, "not instance of CourseChapters");
            }

        }

        Drawable PLAY_BUTTON = ContextCompat.getDrawable(context, R.drawable.play);
        Drawable PAUSE_BUTTON = ContextCompat.getDrawable(context, R.drawable.pause);

        mVideoPlayer = getActivity().findViewById(R.id.video_player);
        mVideoPlayer.setProgressCallback(new BetterVideoProgressCallback() {
            @Override
            public void onVideoProgressUpdate(int position, int duration) {

                if(lastPosition > 10) {
                    lastPosition = 0;
                    Log.d("position", position + "");
                    Log.d("duration", duration + "");
                    new SaveTime(userID, courseID, chapterID, String.valueOf(position)).execute();
                }else{
                    lastPosition++;
                }

            }
        });
        mVideoPlayer.setButtonDrawable(0, PLAY_BUTTON);
        mVideoPlayer.setButtonDrawable(1, PAUSE_BUTTON);
        mVideoPlayer.reset();
        mVideoPlayer.setSource(Uri.parse(firstVideoPath));
        mVideoPlayer.setCaptions(Uri.parse(firstTranscriptPath), CaptionsView.CMime.SUBRIP);
        mVideoPlayer.setLoadingStyle(10);
        mVideoPlayer.enableSwipeGestures();
        mVideoPlayer.setCallback(this);

        adapter = new CourseSectionAdapter(context, mVideoPlayer, courseContentList, fragmentView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        courseContentRecyclerView = fragmentView.findViewById(R.id.course_content_recycler_view);
        courseContentRecyclerView.setLayoutManager(linearLayoutManager);
        courseContentRecyclerView.addItemDecoration(new DividerItemDecoration(courseContentRecyclerView.getContext(), linearLayoutManager.getOrientation()));
        courseContentRecyclerView.setAdapter(adapter);

        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        mVideoPlayer.pause();
    }

    // Methods for the implemented EasyVideoCallback

    @Override
    public void onStarted(BetterVideoPlayer player) {
        Log.i(TAG, "Started");
    }

    @Override
    public void onPaused(BetterVideoPlayer player) {
        Log.i(TAG, "Paused");
    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {
        Log.i(TAG, "Preparing");
    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {
        Log.i(TAG, "Prepared");
    }

    @Override
    public void onBuffering(int percent) {
        Log.i(TAG, "Buffering " + percent);
    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {
        Log.i(TAG, "Error " +e.getMessage());
    }

    @Override
    public void onCompletion(BetterVideoPlayer player) {
        Log.d(TAG, "Completed");
        new SaveCompleted(userID, courseID, chapterID).execute();

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {
        Log.i(TAG, "Controls toggled " + isShowing);
    }

    private class SaveTime extends AsyncTask<Void, Void, String> {

        String userID;
        String courseID;
        String chapterID;
        String time;

        SaveTime(String userID, String courseID, String chapterID, String time) {
            this.userID = userID;
            this.courseID = courseID;
            this.chapterID = chapterID;
            this.time = time;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("courseID", courseID);
            params.put("chapterID", chapterID);
            params.put("time", time);

            Log.d(TAG, params.toString());

            NetworkConnection networkConnection = new NetworkConnection();
            String saveTimeAPI = context.getString(R.string.api_save_time);
            response = networkConnection.performPostCall(saveTimeAPI, params);

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

                        String responseData = jsonObject.getString("response");

                        Log.d(TAG+"re", responseData);

                    } else {

                        Utils.showSnackBar(fragmentView, error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(fragmentView, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(fragmentView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private class SaveCompleted extends AsyncTask<Void, Void, String> {

        String userID;
        String courseID;
        String chapterID;

        SaveCompleted(String userID, String courseID, String chapterID) {
            this.userID = userID;
            this.courseID = courseID;
            this.chapterID = chapterID;
        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("courseID", courseID);
            params.put("chapterID", chapterID);

            Log.d(TAG, params.toString());

            NetworkConnection networkConnection = new NetworkConnection();
            String saveCompletedAPI = context.getString(R.string.api_save_completed);
            response = networkConnection.performPostCall(saveCompletedAPI, params);

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

                        String responseData = jsonObject.getString("response");

                        Log.d(TAG+"re", responseData);

                    } else {

                        Utils.showSnackBar(fragmentView, error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(fragmentView, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(fragmentView, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

}
