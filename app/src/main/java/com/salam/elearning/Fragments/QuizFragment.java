package com.salam.elearning.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.salam.elearning.Adapters.AnswerAdapter;
import com.salam.elearning.Models.Quiz;
import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "QuizFragment";

    private View fragmentView;
    private Context context;
    private ViewGroup answerView;

    private User user;

    private int currentQuizNo;
    private ArrayList<Quiz> sectionQuiz;
    private Quiz quiz;
    private ArrayList<String> answerList;
    private String answer;
    private String question;
    private RecyclerView answerRecyclerView;
    private AnswerAdapter answerAdapter;

    private TextView mQuestioNo;
    private TextView mQuestion;
    private TextView mDragText;

    public static boolean isAnswerCorrect = false;
    public static int answerGiven = 0;

    private LinearLayout recyclerViewContainer;

    private LinearLayout rightAnswerView;

    private Button mCorrectAnswerNextQuestionButton;
    private TextView mCorrectAnswerTextView;

    private LinearLayout wrongAnswerView;
    private ImageView mWrongAnswerChapterImage;
    private TextView mWrongAnswerChapterTitle;
    private TextView mWrongAnswerChapterDuration;
    private Button mWrongAnswerNextQuestionButton;
    private Button mWrongAnswerTryAgainButton;
    private Button mWrongAllQuestionAnsweredSummary;

    private LinearLayout mAllQuestionsAnsweredView;
    private Button mAllQuestionAnsweredViewButton;

    private Button mAllQuestionAnsweredSummary;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_quiz, container, false);

        context = getActivity();
        answerView = fragmentView.findViewById(R.id.answer_view);

        List<User> users = User.getLoggedInUser();
        user = users.get(0);

        mAllQuestionAnsweredSummary = fragmentView.findViewById(R.id.all_questions_answered_summary);
        mAllQuestionAnsweredSummary.setOnClickListener(this);

        currentQuizNo = getArguments().getInt("currentQuizNo");
        quiz = (Quiz) getArguments().getSerializable("currentQuiz");
        sectionQuiz = (ArrayList<Quiz>) getArguments().getSerializable("allQuiz");

        currentQuizNo++;

        if (quiz != null) {
            answerList = new ArrayList<>();
            answerList.add(quiz.getOption1());
            answerList.add(quiz.getOption2());
            answerList.add(quiz.getOption3());
            answerList.add(quiz.getOption4());
        }

        mQuestion = fragmentView.findViewById(R.id.question);
        mQuestion.setText(quiz.getQuestion());

        mQuestioNo = fragmentView.findViewById(R.id.question_no);
        mQuestioNo.setText("Question " + currentQuizNo + " of " + sectionQuiz.size());

        mDragText = fragmentView.findViewById(R.id.drag_text);

        recyclerViewContainer = fragmentView.findViewById(R.id.recycler_view_container);
        rightAnswerView = fragmentView.findViewById(R.id.correct_answer_view);
        mCorrectAnswerNextQuestionButton = fragmentView.findViewById(R.id.next_question_correct);
        mCorrectAnswerNextQuestionButton.setOnClickListener(this);

        wrongAnswerView = fragmentView.findViewById(R.id.wrong_answer_view);
        mWrongAnswerChapterImage = fragmentView.findViewById(R.id.wrong_answer_chapter_image);
        mWrongAnswerChapterTitle = fragmentView.findViewById(R.id.wrong_answer_chapter_title);
        mWrongAnswerChapterDuration = fragmentView.findViewById(R.id.wrong_answer_chapter_duration);

        mWrongAnswerNextQuestionButton = fragmentView.findViewById(R.id.next_question);
        mWrongAnswerNextQuestionButton.setOnClickListener(this);

        mWrongAnswerTryAgainButton = fragmentView.findViewById(R.id.try_again);
        mWrongAnswerTryAgainButton.setOnClickListener(this);

        mWrongAllQuestionAnsweredSummary = fragmentView.findViewById(R.id.all_wrong_questions_answered_summary);

        mWrongAllQuestionAnsweredSummary.setOnClickListener(this);

        mCorrectAnswerTextView = fragmentView.findViewById(R.id.correct_answer);

        mAllQuestionsAnsweredView = fragmentView.findViewById(R.id.all_questions_answered_view);
        mAllQuestionAnsweredViewButton = fragmentView.findViewById(R.id.all_questions_answered_view_button);
        mAllQuestionAnsweredViewButton.setOnClickListener(this);

        answerView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        answerView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
                        mDragText.setVisibility(View.GONE);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        answerView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
                        mDragText.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        answerView.setBackgroundColor(Color.WHITE);
                        break;
                    case DragEvent.ACTION_DROP:

                        mDragText.setVisibility(View.GONE);
                        final View shape = LayoutInflater.from(context).inflate(R.layout.cell_answer, answerView, false);

                        answerView.addView(shape);

                        new SaveAnswer(user.getServerId(), quiz.getServerID(), String.valueOf(answerGiven)).execute();
                        if(isAnswerCorrect){
                            correctAnswer();
                        }else{
                            wrongAnswer();

                        }

                        break;
                    default:
                        break;
                }
                return true;
            }
        });




        answerRecyclerView = fragmentView.findViewById(R.id.answerRecyclerView);

        answerAdapter = new AnswerAdapter(context, answerList, quiz);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        answerRecyclerView.setAdapter(answerAdapter);

        return fragmentView;
    }

    private void wrongAnswer() {

        wrongAnswerView.setVisibility(View.VISIBLE);
        recyclerViewContainer.setVisibility(View.GONE);
        answerView.setVisibility(View.GONE);

        Picasso.with(context)
                .load(quiz.getChapterImage())
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_error)
                .into(mWrongAnswerChapterImage);

        mWrongAnswerChapterTitle.setText(quiz.getChapterTitle());
        mWrongAnswerChapterDuration.setText(quiz.getChapterDuration());

        /*Meaning all questionas have been answered*/
        if(!(currentQuizNo < sectionQuiz.size())){
            mWrongAllQuestionAnsweredSummary.setVisibility(View.VISIBLE);
            mWrongAnswerNextQuestionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void correctAnswer(){

        rightAnswerView.setVisibility(View.VISIBLE);
        recyclerViewContainer.setVisibility(View.GONE);
        answerView.setVisibility(View.GONE);

        mCorrectAnswerTextView.setText(answerList.get(Integer.valueOf(quiz.getAnswer()) - 1));

        if(!(currentQuizNo < sectionQuiz.size())){
            mCorrectAnswerNextQuestionButton.setVisibility(View.GONE);
            mAllQuestionAnsweredSummary.setVisibility(View.VISIBLE);

        }
    }

    private void nextQuestion(){

        if(currentQuizNo < sectionQuiz.size()){
            Bundle args = new Bundle();
            args.putSerializable("currentQuiz", sectionQuiz.get(currentQuizNo));
            args.putSerializable("allQuiz", sectionQuiz);
            args.putInt("currentQuizNo", currentQuizNo);

            QuizFragment quizFragment = new QuizFragment();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0);
            fragmentTransaction.replace(R.id.quiz_fragment_container, quizFragment);
            quizFragment.setArguments(args);
            fragmentTransaction.addToBackStack("Quiz_Fragment");
            fragmentTransaction.commit();
        }else{
            Toast.makeText(context, "No More Questions", Toast.LENGTH_SHORT).show();
        }

    }

    private void goBackToQuestion(){

        wrongAnswerView.setVisibility(View.GONE);
        recyclerViewContainer.setVisibility(View.VISIBLE);
        answerView.setVisibility(View.VISIBLE);

        if(!(currentQuizNo < sectionQuiz.size())){
            mWrongAnswerNextQuestionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.next_question_correct:

                nextQuestion();
                break;

            case R.id.next_question:

                nextQuestion();
                break;

            case R.id.try_again:
                goBackToQuestion();
                break;

            case R.id.all_questions_answered_summary:
                mAllQuestionsAnsweredView.setVisibility(View.VISIBLE);
                break;

            case R.id.all_wrong_questions_answered_summary:
                mAllQuestionsAnsweredView.setVisibility(View.VISIBLE);
                break;

            case R.id.all_questions_answered_view_button:
                getActivity().onBackPressed();
                break;
        }
    }

    private class SaveAnswer extends AsyncTask<Void, Void, String> {

        String userID;
        String quizID;
        String answer;

        SaveAnswer(String userID, String quizID, String answer) {
            this.userID = userID;
            this.quizID = quizID;
            this.answer = answer;

        }


        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("userID", userID);
            params.put("quizID", quizID);
            params.put("answer", answer);

            NetworkConnection networkConnection = new NetworkConnection();
            String saveAnswerAPI = context.getString(R.string.api_save_answer);
            response = networkConnection.performPostCall(saveAnswerAPI, params);

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

                        if(!responseData.equalsIgnoreCase("Saved Successfully")){
                            Utils.showSnackBar(fragmentView, responseData, Snackbar.LENGTH_SHORT);
                        }

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
