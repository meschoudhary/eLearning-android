package com.salam.elearning.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.salam.elearning.LoginActivity;
import com.salam.elearning.MainActivity;
import com.salam.elearning.R;
import com.salam.elearning.SearchActivity;
import com.salam.elearning.Utils.TypefaceSpan;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment implements View.OnClickListener {

    private View fragmentView;
    private Context context;

    private OnDataPass dataPasser;

    private String query;

    private Button[] levelButtonsArray = new Button[4];
    private Button unselectedLevelButton;
    private int[] levelButtonIds = {R.id.filter_level_option_1, R.id.filter_level_option_2, R.id.filter_level_option_3, R.id.filter_level_option_4};
    private int[] levelButtonString = {R.string.filter_level_option_1, R.string.filter_level_option_2, R.string.filter_level_option_3, R.string.filter_level_option_4};
    private Button initialSelectedLevelButton;

    private Button[] typeButtonsArray = new Button[3];
    private Button unselectedTypeButton;
    private int[] typeButtonIds = {R.id.filter_type_option_1, R.id.filter_type_option_2, R.id.filter_type_option_3};
    private int[] typeButtonString = {R.string.filter_type_option_1, R.string.filter_type_option_2, R.string.filter_type_option_3};
    private Button initialSelectedTypeButton;

    private Button[] durationButtonsArray = new Button[7];
    private Button unselectedDurationButton;
    private int[] durationButtonIds = {R.id.filter_duration_option_1, R.id.filter_duration_option_2, R.id.filter_duration_option_3,
            R.id.filter_duration_option_4,  R.id.filter_duration_option_5,  R.id.filter_duration_option_6,  R.id.filter_duration_option_7};
    private int[] durationButtonString = {R.string.filter_duration_default_selected_1, R.string.filter_duration_default_selected_2, R.string.filter_duration_default_selected_3,
            R.string.filter_duration_default_selected_4,  R.string.filter_duration_default_selected_5,  R.string.filter_duration_default_selected_6,  R.string.filter_duration_default_selected_7};
    private Button initialSelectedDurationButton;

    private String levelValue = "All";
    private String typeValue = "all";
    private String durationValue = "0";

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SpannableString actionBarTitle = new SpannableString("Refine");
        actionBarTitle.setSpan(new TypefaceSpan(getActivity(), "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((SearchActivity) getActivity()).getSupportActionBar().setTitle(actionBarTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_filter, container, false);
        context = getActivity();

        query = getArguments().getString("query");
        levelValue = getArguments().getString("level");
        typeValue = getArguments().getString("type");
        durationValue = getArguments().getString("duration");

        for(int i = 0; i < levelButtonsArray.length; i++){
            levelButtonsArray[i] = fragmentView.findViewById(levelButtonIds[i]);
            levelButtonsArray[i].setOnClickListener(this);
            if(levelValue.equalsIgnoreCase(context.getResources().getString(levelButtonString[i]))){
                initialSelectedLevelButton  = levelButtonsArray[i];
            }
        }
        unselectedLevelButton = levelButtonsArray[0];
        setLevelButtonFocus(unselectedLevelButton, initialSelectedLevelButton);


        for(int i = 0; i < typeButtonsArray.length; i++){
            typeButtonsArray[i] = fragmentView.findViewById(typeButtonIds[i]);
            typeButtonsArray[i].setOnClickListener(this);
            if(typeValue.equalsIgnoreCase(context.getResources().getString(typeButtonString[i]).toLowerCase())){
                initialSelectedTypeButton  = typeButtonsArray[i];
            }
        }
        unselectedTypeButton = typeButtonsArray[0];
        setTypeButtonFocus(unselectedTypeButton, initialSelectedTypeButton);

        for(int i = 0; i < durationButtonsArray.length; i++){
            durationButtonsArray[i] = fragmentView.findViewById(durationButtonIds[i]);
            durationButtonsArray[i].setOnClickListener(this);
            if(durationValue.equalsIgnoreCase(context.getResources().getString(durationButtonString[i]))){
                initialSelectedDurationButton  = durationButtonsArray[i];
            }
        }
        unselectedDurationButton = durationButtonsArray[0];
        setDurationButtonFocus(unselectedDurationButton, initialSelectedDurationButton);

        return fragmentView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.done).setVisible(true);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.done:
                getActivity().onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            /*First button of level filters*/
            case R.id.filter_level_option_1 :
                setLevelButtonFocus(unselectedLevelButton, levelButtonsArray[0]);
                levelValue = "All";
                break;

            /*Second button of level filters*/
            case R.id.filter_level_option_2 :
                setLevelButtonFocus(unselectedLevelButton, levelButtonsArray[1]);
                levelValue = "Beginner";
                break;

            /*Third button of level filters*/
            case R.id.filter_level_option_3 :
                setLevelButtonFocus(unselectedLevelButton, levelButtonsArray[2]);
                levelValue = "Intermediate";
                break;

            /*Fourth button of level filters*/
            case R.id.filter_level_option_4 :
                setLevelButtonFocus(unselectedLevelButton, levelButtonsArray[3]);
                levelValue = "Advanced";
                break;

            /*First button of type filters*/
            case R.id.filter_type_option_1 :
                setTypeButtonFocus(unselectedTypeButton, typeButtonsArray[0]);
                typeValue = "all";
                break;

            /*Second button of type filters*/
            case R.id.filter_type_option_2 :
                setTypeButtonFocus(unselectedTypeButton, typeButtonsArray[1]);
                typeValue = "course";
                break;

            /*Third button of type filters*/
            case R.id.filter_type_option_3 :
                setTypeButtonFocus(unselectedTypeButton, typeButtonsArray[2]);
                typeValue = "videos";
                break;


            case R.id.filter_duration_option_1 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[0]);
                durationValue = "0";
                break;

            case R.id.filter_duration_option_2 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[1]);
                durationValue = "1";
                break;

            case R.id.filter_duration_option_3 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[2]);
                durationValue = "2";
                break;

            case R.id.filter_duration_option_4 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[3]);
                durationValue = "3";
                break;

            case R.id.filter_duration_option_5 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[4]);
                durationValue = "4";
                break;

            case R.id.filter_duration_option_6 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[5]);
                durationValue = "5";
                break;

            case R.id.filter_duration_option_7 :
                setDurationButtonFocus(unselectedDurationButton, durationButtonsArray[6]);
                durationValue = "6";
                break;
        }

        passData(levelValue, typeValue, durationValue);

    }

    private void setLevelButtonFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(ContextCompat.getColor(context, R.color.black));
        btn_unfocus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGrey));
        btn_focus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        btn_focus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        this.unselectedLevelButton = btn_focus;
    }

    private void setTypeButtonFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(ContextCompat.getColor(context, R.color.black));
        btn_unfocus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGrey));
        btn_focus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        btn_focus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        this.unselectedTypeButton = btn_focus;
    }

    private void setDurationButtonFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(ContextCompat.getColor(context, R.color.black));
        btn_unfocus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGrey));
        btn_focus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        btn_focus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        this.unselectedDurationButton = btn_focus;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SpannableString actionBarTitle = new SpannableString("You searched for : " + query);
        actionBarTitle.setSpan(new TypefaceSpan(getActivity(), "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((SearchActivity) getActivity()).getSupportActionBar().setTitle(actionBarTitle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public interface OnDataPass {
        public void onDataPass(String data, String type, String duration);
    }

    public void passData(String level, String type, String duration) {
        dataPasser.onDataPass(level, type, duration);
    }
}
