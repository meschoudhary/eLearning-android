package com.salam.elearning.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseLikeAdapter extends RecyclerView.Adapter<CourseLikeAdapter.Holder> {

    private ArrayList<User> courseLikedByUsers;
    private Context context;

    public CourseLikeAdapter(Context context, ArrayList<User> courseLikedByUsers) {
        this.context = context;
        this.courseLikedByUsers = courseLikedByUsers;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_like_course, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        User user = courseLikedByUsers.get(position);

        int totalSize = getItemCount();

        if(totalSize > 4){

            if(position < 4){

                Picasso.with(context)
                        .load(user.getImage())
                        .placeholder(R.drawable.cover_placeholder)
                        .error(R.drawable.cover_error)
                        .into(holder.mUserImage);

            }else if(position == 4){

                Picasso.with(context)
                        .load(R.drawable.more)
                        .placeholder(R.drawable.cover_placeholder)
                        .error(R.drawable.cover_error)
                        .into(holder.mUserImage);

            }else{

                holder.mUserImage.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return courseLikedByUsers.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        CircleImageView mUserImage;

        Holder(View itemView) {
            super(itemView);

            mUserImage = itemView.findViewById(R.id.like_user_image);

        }
    }
}


