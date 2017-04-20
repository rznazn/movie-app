package com.example.android.movieapp;

/**
 * Created by sport on 4/20/2017.
 */

public class MovieReview {

    /**
     * custom object to store the review results
     */

    private String reviewerName;
    private String reviewText;

    /**
     * constructor
     * @param name of the reviewer
     * @param review text of the review
     */
    public MovieReview(String name, String review){
        reviewerName = name;
        reviewText = review;
    }

    /**
     * getter methods
     * @return
     */
    public String getReviewerName(){return reviewerName;}
    public String getReviewText() {return reviewText;}
}
