package com.example.android.movieapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sport on 3/6/2017.
 * Adapter assigns values and traits to the views in the main activity
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    public TextView mIdTextView;
    private String mId;
    private String mTitle;
    private String mOverview;

    private final MovieAdapterOnClickHandler mClickHandler;
    private  ArrayList<MovieTagObject> mMovieTagObjects;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, ArrayList<MovieTagObject> movieTagObjects) {
        mClickHandler = clickHandler;
        mMovieTagObjects = movieTagObjects;

    }


    /**
     * defines the view to inflate for the RecyclerView items
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

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        final MovieTagObject currentMovie = mMovieTagObjects.get(position);

        mTitle = currentMovie.getTitle();
        mIdTextView.setText(mTitle);

    }


    /**
     * This item returns how many views to inflate
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (mMovieTagObjects == null) {
            return 0;
        } else {
            return mMovieTagObjects.size();
        }
    }

    public void setMovieData(ArrayList<MovieTagObject> movies){
        mMovieTagObjects = movies;
        notifyDataSetChanged();

    }

    /**
     * Caches the children views for the RecyclerView
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * @param itemView this mehod sets an onCLickListener to the input view
         */
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mIdTextView = (TextView) itemView.findViewById(R.id.tv_grid_item);
            itemView.setOnClickListener(this);

        }

        /**
         * This is the method called when an individual view is clicked
         *
         * @param v is the view that was clicked
         */
        @Override
        public void onClick(View v) {

            /**
             * TODO remove this Toast message it is only here for development use
             */
            Context context = v.getContext();
            int id = getAdapterPosition();
            Toast.makeText(context, "click" + id, Toast.LENGTH_LONG).show();
            MovieTagObject currentMovie = mMovieTagObjects.get(id);
            /**
             * This AlertDialog will display the movie details
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View displayView = inflater.inflate(R.layout.detail_layout, null);
            builder.setView(displayView);
            builder.setMessage(currentMovie.getTitle() + "\n" + currentMovie.getOverview());
            builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            AlertDialog ad = builder.create();
            ad.show();
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(View v);
    }
}
