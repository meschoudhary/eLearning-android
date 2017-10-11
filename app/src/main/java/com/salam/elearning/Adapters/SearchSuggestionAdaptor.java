package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salam.elearning.CourseActivity;
import com.salam.elearning.R;
import com.squareup.picasso.Picasso;

public class SearchSuggestionAdaptor extends SimpleCursorAdapter implements View.OnClickListener {

    private static final String tag = SearchSuggestionAdaptor.class.getName();
    private Context context = null;
    private Cursor Cursor;
    private String userID;

    public SearchSuggestionAdaptor(Context context, int layout, Cursor c, String[] from, int[] to, int flags, String userID) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.userID = userID;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Cursor = cursor;
        RelativeLayout suggestionContainer = view.findViewById(R.id.search_suggestion);
        ImageView imageView = view.findViewById(R.id.course_pic);
        TextView title = view.findViewById(R.id.title);
        TextView type = view.findViewById(R.id.type);

        if(!cursor.getString(2).isEmpty()){
            Picasso.with(context)
                    .load(cursor.getString(2))
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_error)
                    .into(imageView);
        }else{
            imageView.setVisibility(View.GONE);
        }


        title.setText(cursor.getString(3));
        type.setText(cursor.getString(4));

        /*if(!cursor.getString(5).isEmpty()){
            suggestionContainer.setOnClickListener(this);
        }*/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.search_suggestion:

                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("courseID", Cursor.getString(1));
                intent.putExtra("userID", userID);
                context.startActivity(intent);

                break;
        }

    }
}
