package com.example.android.movieapp;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by sport on 3/12/2017.
 * dynamically calculate the appropriate spanCount for the gridLayoutManager
 */

public class spanUtility {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
