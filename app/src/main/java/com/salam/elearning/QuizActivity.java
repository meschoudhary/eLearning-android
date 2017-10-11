package com.salam.elearning;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;

import com.salam.elearning.Fragments.QuizFragment;
import com.salam.elearning.Models.Quiz;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {


    private static final String TAG = "QuizActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Bundle b = getIntent().getExtras();

        ArrayList<Quiz> sectionQuiz = (ArrayList<Quiz>) b.getSerializable("quiz");

        Log.d(TAG, "Got Data");

        Bundle args = new Bundle();
        int currentQuizNo = 0;
        args.putSerializable("currentQuiz", sectionQuiz.get(currentQuizNo));
        args.putSerializable("allQuiz", sectionQuiz);
        args.putInt("currentQuizNo", currentQuizNo);


        QuizFragment quizFragment = new QuizFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0);
        fragmentTransaction.replace(R.id.quiz_fragment_container, quizFragment);
        quizFragment.setArguments(args);
        fragmentTransaction.addToBackStack("Quiz_Fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
