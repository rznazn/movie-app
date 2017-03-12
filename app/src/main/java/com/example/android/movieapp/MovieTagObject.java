package com.example.android.movieapp;

/**
 * Created by sport on 3/11/2017.
 */

public class MovieTagObject extends Object{
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mImagePath;

    public MovieTagObject(String id, String title, String overview, String imagePath){
        mId = id;
        mTitle = title;
        mOverview = overview;
        mImagePath = imagePath;
    }

    public String getId(){
        return mId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getOverview(){
        return mOverview;
    }
    public String getImagePath(){
        return mImagePath;
    }
}
