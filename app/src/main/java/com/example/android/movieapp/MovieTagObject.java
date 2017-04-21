package com.example.android.movieapp;

import android.graphics.Bitmap;

/**
 * Created by sport on 3/11/2017.
 * This Custom Object stores the relevant values from the MovieDb Json
 */

public class MovieTagObject extends Object{
    /**
     * object variables for storing values
     */
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mImagePath;
    private String mReleaseDate;
    private String mVoterAverage;
    private Bitmap mPosterImage;

    /**
     * @param id Id of the movie in the movieDB api
     * @param title title of the movie
     * @param overview plot synopis
     * @param imagePath string that locates the movie poster in the Movie DB
     * @param releaseDate of the movie
     * @param voterAverage of the movie
     */
    public MovieTagObject(String id, String title, String overview, String imagePath, String releaseDate,
                          String voterAverage, Bitmap posterImage){
        mId = id;
        mTitle = title;
        mOverview = overview;
        mImagePath = imagePath;
        mReleaseDate = releaseDate;
        mVoterAverage = voterAverage;
        mPosterImage = posterImage;
    }

    /**
     * getter methods for the values stored in the MovieTagObject
     * @return
     */
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
    public String getReleaseDate(){
        return mReleaseDate;
    }
    public String getVoterAverage(){ return mVoterAverage; }
    public Bitmap getPosterImage(){return mPosterImage;}
}
