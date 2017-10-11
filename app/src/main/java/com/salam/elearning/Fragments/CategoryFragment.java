package com.salam.elearning.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.salam.elearning.Adapters.CategoryAdapter;
import com.salam.elearning.LoginActivity;
import com.salam.elearning.MainActivity;
import com.salam.elearning.Models.Category;
import com.salam.elearning.Models.SubCategory;
import com.salam.elearning.Models.User;
import com.salam.elearning.R;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryFragment extends Fragment {


    private String TAG = "CategoryFragment";
    private View fragmentView;

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;

    private Context context;

    private ArrayList<Category> categories;
    private ArrayList<SubCategory> subCategories;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_category, container, false);

        context = getActivity();

        categories = new ArrayList<>();
        subCategories = new ArrayList<>();

        recyclerView = fragmentView.findViewById(R.id.category_sub_category_recyclerview);

        new GetCategory().execute();

        return fragmentView;
    }

    private class GetCategory extends AsyncTask<Void, Void, String> {

        GetCategory() {}

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);

            NetworkConnection networkConnection = new NetworkConnection();
            String getCategorydApi = context.getString(R.string.api_get_category);
            response = networkConnection.performPostCall(getCategorydApi, params);

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

                        JSONArray data = jsonObject.getJSONArray("response");

                        categories = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++){

                            JSONObject category = data.getJSONObject(i);

                            JSONArray subCategoryAll = category.getJSONArray("subcategory");

                            for (int j = 0; j< subCategoryAll.length(); j++){

                                JSONObject subCategory = subCategoryAll.getJSONObject(j);

                                subCategories.add(new SubCategory(subCategory.getString("id"), subCategory.getString("name")));
                            }

                            categories.add(new Category(category.getString("id"), category.getString("name"), subCategories));
                            subCategories = new ArrayList<>();
                        }

                        adapter = new CategoryAdapter(context, categories);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                        Log.d(TAG, String.valueOf(categories));

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
