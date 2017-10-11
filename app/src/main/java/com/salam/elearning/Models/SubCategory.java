package com.salam.elearning.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubCategory implements Parcelable {

    private String subCategoryServerID;
    private String subCategoryName;

    public SubCategory(String subCategoryServerID, String subCategoryName) {
        this.subCategoryServerID = subCategoryServerID;
        this.subCategoryName = subCategoryName;
    }

    protected SubCategory(Parcel in) {
        subCategoryServerID = in.readString();
        subCategoryName = in.readString();
    }

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    public String getSubCategoryServerID() {
        return subCategoryServerID;
    }

    public void setSubCategoryServerID(String subCategoryServerID) {
        this.subCategoryServerID = subCategoryServerID;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subCategoryName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
