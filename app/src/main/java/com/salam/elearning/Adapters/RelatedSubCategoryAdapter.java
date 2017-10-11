package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salam.elearning.CategoryViewActivity;
import com.salam.elearning.Models.SubCategory;
import com.salam.elearning.R;

import java.util.List;

public class RelatedSubCategoryAdapter extends RecyclerView.Adapter<RelatedSubCategoryAdapter.Holder> {

    private List<SubCategory> subCategories;
    private Context context;


    public RelatedSubCategoryAdapter(Context context, List<SubCategory> subCategories) {

        this.context = context;
        this.subCategories = subCategories;

    }

    public void refreshAdapter(List<SubCategory> subCategories){
        this.subCategories = subCategories;
        notifyDataSetChanged();

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_related_sub_category, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        final SubCategory subCategory = subCategories.get(position);
        //holder.Bind(subCategory);

        holder.mRelatedSubCategoryName.setText(subCategory.getSubCategoryName());

        /*holder.mRelatedSubCategoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryViewActivity.class);
                intent.putExtra("subCategoryID", subCategory.getSubCategoryServerID());
                intent.putExtra("subCategoryName", subCategory.getSubCategoryName());
                context.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView mRelatedSubCategoryName;
        //private LinearLayout mRelatedSubCategoryContainer;

        public Holder(View itemView) {
            super(itemView);
            mRelatedSubCategoryName = itemView.findViewById(R.id.related_sub_category_name);
            //mRelatedSubCategoryContainer = itemView.findViewById(R.id.related_sub_category_container);

        }

        private void Bind(final SubCategory subCategory){
            mRelatedSubCategoryName.setText(subCategory.getSubCategoryName());

            /*mRelatedSubCategoryContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CategoryViewActivity.class);
                    intent.putExtra("subCategoryID", subCategory.getSubCategoryServerID());
                    intent.putExtra("subCategoryName", subCategory.getSubCategoryName());
                    context.startActivity(intent);
                }
            });*/

        }
    }
}
