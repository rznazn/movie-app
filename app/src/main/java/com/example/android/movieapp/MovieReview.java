package com.example.android.movieapp;

/**
 * Created by sport on 4/20/2017.
 */

public class MovieReview {

    private String reviewerName;
    private String reviewText;

    public MovieReview(String name, String review){
        reviewerName = name;
        reviewText = review;
    }

    public String getReviewerName(){return reviewerName;}
    public String getReviewText() {return reviewText;}
}
