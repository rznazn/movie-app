package com.example.android.movieapp;

import java.util.ArrayList;

/**
 * Created by sport on 3/12/2017.
 */

public class preferences {
    public static boolean attributionShown = false;
    public static String sortPreference = "popular";
    public static ArrayList<MovieTagObject> mPopularMovies = new ArrayList<>();
    public static ArrayList<MovieTagObject> mHighestRatedMovies = new ArrayList<>();
    public static ArrayList<MovieTagObject> mFavoriteMovies = new ArrayList<>();

}
