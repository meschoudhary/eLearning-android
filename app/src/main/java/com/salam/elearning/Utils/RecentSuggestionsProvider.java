package com.salam.elearning.Utils;

import android.content.SearchRecentSuggestionsProvider;

import com.salam.elearning.SearchActivity;

public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.salam.elearning.Utils.RecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
