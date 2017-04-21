package com.example.android.movieapp.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.DetailLayoutActivity;
import com.example.android.movieapp.MovieTagObject;
import com.example.android.movieapp.R;

import java.util.ArrayList;

/**
 * Created by sport on 3/6/2017.
 * Adapter assigns values and traits to the views in the main activity
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    /**
     * variables for the MovieAdapter object
     */
    private final MovieAdapterOnClickHandler mClickHandler;
    private  ArrayList<MovieTagObject> mMovieTagObjects;

    /**
     * Constructor to instantiate the object Variable
     * @param clickHandler
     * @param movieTagObjects this object is essential to this Class function and contains all the
     *                        movie data
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, ArrayList<MovieTagObject> movieTagObjects) {
        mClickHandler = clickHandler;
        mMovieTagObjects = movieTagObjects;

    }


    /**
     * defines the view to inflate for the RecyclerView Holders
     *
     * @param parent
     * @param viewType
     * @return returns the individual layout item with it's contents
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * extract values and set content from the current index in the recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        /**
         * currentMovie get current object from arraylist
         */
        MovieTagObject currentMovie = mMovieTagObjects.get(position);
        /**
         * get image and use methods from MovieDBJsonUtils to loadImage from the Uri built
         * by the NetworkUtils method into the ImageView, posterImage.
         * then set that value to the mPosterImageView from the viewholder object.
         */
        Bitmap bitmap = currentMovie.getPosterImage();
        holder.mPosterImageView.setImageBitmap(bitmap);
//        String imagePath = currentMovie.getImagePath();
//        try {
//            ImageView posterImage = MovieDBJsonUtils.loadImageFromJson(holder.mPosterImageView,
//                    NetworkUtils.buildImageResUri(imagePath));
//            holder.mPosterImageView.equals(posterImage);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        holder.mIdTextView.setText(currentMovie.getTitle());

    }


    /**
     * This item returns how many views to inflate by getting the size of the data ArrayList
     * @return size of the mMovietagObjects array list
     */
    @Override
    public int getItemCount() {
        if (mMovieTagObjects == null) {
            return 0;
        } else {
            return mMovieTagObjects.size();
        }
    }

    /**
     * @param movies set new data set to mMovieTagObjects ArrayList and notify adapter
     *               to reset
     */
    public void setMovieData(ArrayList<MovieTagObject> movies){
        mMovieTagObjects = movies;
        notifyDataSetChanged();

    }

    /**
     * Caches the children views for the RecyclerView
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * declare the gridItem views here to prevent shuffling of data on scrolling
         */
        public ImageView mPosterImageView;
        public TextView mIdTextView;

        /**
         * @param itemView this mehod sets an onCLickListener to the input view
         *                 Instantiate views of the ViewHolder object
         */
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_grid_item);
            mIdTextView = (TextView) itemView.findViewById(R.id.tv_grid_item);
            itemView.setOnClickListener(this);

        }

        /**
         * This is the method called when an individual view is clicked
         * extract the data from the  selected movie and open and pass to the
         * opened detail activity
         * @param v is the view that was clicked
         */
        @Override
        public void onClick(View v) {

            Intent detailIntent = new Intent(v.getContext(), DetailLayoutActivity.class);
            MovieTagObject currentMovie = mMovieTagObjects.get(getAdapterPosition());
            detailIntent.putExtra("id", currentMovie.getId());
            detailIntent.putExtra(v.getContext().getString(R.string.title),currentMovie.getTitle());
            detailIntent.putExtra(v.getContext().getString(R.string.release_date), currentMovie.getReleaseDate());
            detailIntent.putExtra(v.getContext().getString(R.string.voter_average), currentMovie.getVoterAverage());
            detailIntent.putExtra(v.getContext().getString(R.string.plot), currentMovie.getOverview());
            detailIntent.putExtra(v.getContext().getString(R.string.image_path), currentMovie.getImagePath());
            v.getContext().startActivity(detailIntent);

        }
    }

    /**
     * this is required for this class's click funcition however all click functions are handled
     * elseWhere
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(View v);
    }
}
