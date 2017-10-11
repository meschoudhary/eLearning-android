package com.salam.elearning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salam.elearning.CategoryViewActivity;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.Category;
import com.salam.elearning.Models.SubCategory;
import com.salam.elearning.R;
import com.salam.elearning.Utils.TypefaceSpan;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public class CategoryAdapter extends ExpandableRecyclerViewAdapter<CategoryViewHolder, SubCategoryViewHolder> {

    private Context context;
    public CategoryAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public SubCategoryViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sub_category, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SubCategoryViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final SubCategory subCategory = ((Category) group).getItems().get(childIndex);
        holder.onBind(context, subCategory);
    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setCategoryTitleTitle(context, group);
    }
}

class SubCategoryViewHolder extends ChildViewHolder {

    public TextView subCategoryName;
    private LinearLayout subCategoryContainer;

    SubCategoryViewHolder(View itemView) {
        super(itemView);
        subCategoryName = itemView.findViewById(R.id.sub_category_name);
        subCategoryContainer = itemView.findViewById(R.id.sub_category_container);
    }

    public void onBind(final Context context, final SubCategory subcategory) {
        SpannableString actionBarTitle = new SpannableString(subcategory.getSubCategoryName());
        actionBarTitle.setSpan(new TypefaceSpan(context, "SourceSansPro-Regular.ttf"), 0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        subCategoryName.setText(actionBarTitle);

        subCategoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryViewActivity.class);
                intent.putExtra("subCategoryID", subcategory.getSubCategoryServerID());
                intent.putExtra("subCategoryName", subcategory.getSubCategoryName());
                context.startActivity(intent);
            }
        });
    }
}

class CategoryViewHolder extends GroupViewHolder {

    private TextView categoryName;

    CategoryViewHolder(View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.category_name);
    }

    void setCategoryTitleTitle(Context context, ExpandableGroup group) {
        SpannableString actionBarTitle = new SpannableString(group.getTitle());
        actionBarTitle.setSpan(new TypefaceSpan(context, "SourceSansPro-Semibold.ttf"), 0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        categoryName.setText(actionBarTitle);
    }
}

