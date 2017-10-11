package com.salam.elearning.Models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Category extends ExpandableGroup<SubCategory> {

    private String categoryServerID;
    private String categoryName;
    private List<SubCategory> subCategories;

    public Category(String categoryServerID, String categoryName, List<SubCategory> items) {
        super(categoryName, items);
        this.categoryServerID = categoryServerID;
        this.categoryName = categoryName;
        this.subCategories = items;
    }

    public String getCategoryServerID() {
        return categoryServerID;
    }

    public void setCategoryServerID(String categoryServerID) {
        this.categoryServerID = categoryServerID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
