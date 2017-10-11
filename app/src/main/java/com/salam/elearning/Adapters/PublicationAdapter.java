package com.salam.elearning.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salam.elearning.R;

import java.util.ArrayList;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.Holder> {

    private ArrayList<String> allPublications;
    private Context context;

    public PublicationAdapter(Context context, ArrayList<String> allPublications) {
        this.allPublications = allPublications;
        this.context = context;
    }

    @Override
    public PublicationAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_publication, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(PublicationAdapter.Holder holder, int position) {
        String publicationn = allPublications.get(position);

        holder.mPublicationName.setText(publicationn);
    }

    @Override
    public int getItemCount() {
        return allPublications.size();
    }

    public void refreshAdapter(ArrayList<String> allPublications) {

        this.allPublications = allPublications;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView mPublicationName;

        Holder(View itemView) {
            super(itemView);

            mPublicationName = itemView.findViewById(R.id.user_publication);

        }
    }
}
