package com.example.android.movieapp.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movieapp.MovieReview;
import com.example.android.movieapp.R;

import java.util.ArrayList;

/**
 * Created by sport on 4/20/2017.
 */

public class MovieReviewRecyclerViewAdapter extends RecyclerView.Adapter<MovieReviewRecyclerViewAdapter.MovieReviewHolder>{

    /**
     * member variable for the adpater's data set
     */
    private ArrayList<MovieReview> mMovieReviews = new ArrayList<>();

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieReviewHolder viewHolder = new MovieReviewHolder(view);

        return viewHolder;
    }

    /**
     * bind movie review object parts to the respective textviews
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MovieReviewHolder holder, int position) {
        MovieReview movieReview = mMovieReviews.get(position);
        String nameForDisplay = "\n\n" + movieReview.getReviewerName() + ":\n";
        holder.reviewerName.setText(nameForDisplay);
        String reviewNameForDisplay = movieReview.getReviewText() + "\n\n";
        holder.reviewText.setText(reviewNameForDisplay);
    }

    /**
     *
     * @return the size of the data set
     */
    @Override
    public int getItemCount() {
        return mMovieReviews.size();
    }

    /**
     * view holder object
     */
    class MovieReviewHolder extends RecyclerView.ViewHolder {

        TextView reviewerName;
        TextView reviewText;

        public MovieReviewHolder(View itemView) {
            super(itemView);

            reviewerName = (TextView) itemView.findViewById(R.id.reviewer_name);
            reviewText = (TextView) itemView.findViewById(R.id.review_text);
        }


    }

    /**
     *
     * @param movieReviews instate a new data set and notify adapter of the change
     */
    public void setmMovieReviews(ArrayList<MovieReview> movieReviews){
        mMovieReviews = movieReviews;
        notifyDataSetChanged();
    }
}
