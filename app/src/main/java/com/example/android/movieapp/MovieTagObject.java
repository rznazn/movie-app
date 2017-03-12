package com.example.android.movieapp;

/**
 * Created by sport on 3/11/2017.
 */

public class MovieTagObject extends Object{
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mImagePath;
    private String mReleaseDate;
    private String mVoterAverage;

    public MovieTagObject(String id, String title, String overview, String imagePath, String releaseDate,
                          String voterAverage){
        mId = id;
        mTitle = title;
        mOverview = overview;
        mImagePath = imagePath;
        mReleaseDate = releaseDate;
        mVoterAverage = voterAverage;
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
    public String getReleaseDate(){
        return mReleaseDate;
    }
    public String getVoterAverage(){
        return mVoterAverage;
    }
}
