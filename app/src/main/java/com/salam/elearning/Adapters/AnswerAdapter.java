package com.salam.elearning.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salam.elearning.Fragments.QuizFragment;
import com.salam.elearning.Models.Quiz;
import com.salam.elearning.R;

import java.util.ArrayList;


public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.Holder> {

    private Context context;
    private ArrayList<String> answerList;
    private Quiz quiz;

    public AnswerAdapter(Context context, ArrayList<String> answerList, Quiz quiz) {
        this.context = context;
        this.answerList = answerList;
        this.quiz = quiz;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_answer, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        String answer = answerList.get(position);
        holder.onBind(answer, position);


    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView mAnswer;

        public Holder(View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer);

        }

        void onBind(String answer, final int position){

            mAnswer.setText(answer);
            mAnswer.setTag(position);

            final View answerView = mAnswer;

            mAnswer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    final String item = answerList.get(position);
                    final DragData state = new DragData(item, answerView.getWidth(), answerView.getHeight());

                    state.DragItem(position, mAnswer, answerView, state);
                    return true;
                }
            });
        }
    }

    private class DragData {

        final String item;
        final int width;
        public final int height;

        DragData(String item, int width, int height) {
            this.item= item;
            this.width = width;
            this.height = height;
        }

        void DragItem(int position, TextView mAnswer, View answerView, DragData state){

            QuizFragment.isAnswerCorrect = ((position + 1) == Integer.valueOf(quiz.getAnswer()));
            QuizFragment.answerGiven = position + 1;
            if(!QuizFragment.isAnswerCorrect){
                mAnswer.setLongClickable(false);
                mAnswer.setClickable(false);
                mAnswer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight));
            }


            final View.DragShadowBuilder shadow = new View.DragShadowBuilder(answerView);
            ViewCompat.startDragAndDrop(answerView, null, shadow, state, 0);
        }
    }
}
