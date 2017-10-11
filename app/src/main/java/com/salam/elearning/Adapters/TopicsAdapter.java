package com.salam.elearning.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salam.elearning.R;

import java.util.ArrayList;
import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.Holder> {

    private ArrayList<String> allTopics;
    private Context context;

    public TopicsAdapter(Context context, ArrayList<String> allTopics) {

        this.allTopics = allTopics;
        this.context = context;
    }

    @Override
    public TopicsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_topics, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(TopicsAdapter.Holder holder, int position) {

        String topic = allTopics.get(0);

        holder.mTopicName.setText(topic);
    }

    @Override
    public int getItemCount() {
        return allTopics.size();
    }

    public void refreshAdapter(ArrayList<String> allTopics) {

        this.allTopics = allTopics;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView mTopicName;

        Holder(View itemView) {
            super(itemView);

            mTopicName = itemView.findViewById(R.id.user_topics);

        }
    }
}
