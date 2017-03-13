package com.example.android.movieapp;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

/**
 * Application Main Activity
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private ConnectivityManager mConnectivityManager;

    /**
     * Variables for use in handling the UI layouts/views/layout manager/adapter
     */
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView mErrorScreen;
    private ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;
    private int spanCount = 2;
    private ArrayList<MovieTagObject> mMovieTagObjects = null;

    /**
     * Variable to be used in determining the sort order for the search
     */
    public static String sortPreference = "popular";
    public static String response = "failed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * AlertDialog for Movie DB attribution
         */
        if (!preferences.attributionShown) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.movieDB_disclaimer);
            AlertDialog ad = builder.create();
            ad.show();
            preferences.attributionShown = true;
        }

        /**
         * this method determines the spanCount for the current device
         */
        spanCount = spanUtility.calculateNoOfColumns(this);

        /**
         * instantiate variables and assign layout manager and adapter.
         */


        mLayoutManager = new GridLayoutManager(this, spanCount
                , GridLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorScreen = (TextView) findViewById(R.id.tv_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        showProgressBar();
        new GetMoviesTask().execute(sortPreference);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mMovieAdapter = new MovieAdapter(this, mMovieTagObjects);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private final void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorScreen.setVisibility(View.INVISIBLE);
    }

    private final void showErrorScreen() {
        mErrorScreen.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private final void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorScreen.setVisibility(View.INVISIBLE);
    }

    /**
     * inflate options menu with the options for sorting by popularity or ranking
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Select the sortBy Preference and refresh the layout
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.action_sort_by_popular:
                sortPreference = "popular";
                new GetMoviesTask().execute(sortPreference);
                break;
            case R.id.action_sort_by_highest_rated:
                sortPreference = "top_rated";
                new GetMoviesTask().execute(sortPreference);
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {

        /**
         * This method is defined in the MovieAdapter class
         */
    }

    /**
     * Async Task the makes network request to get movie data
     */
    public class GetMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL movieUrl = NetworkUtils.buildUrl(sortBy);

            try {
                response = NetworkUtils
                        .getResponseFromHttpUrl(movieUrl);

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null){
                showErrorScreen();
            }else {
                try {
                    mMovieTagObjects = MovieDBJsonUtils.translateStringToArrayList(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMovieAdapter.setMovieData(mMovieTagObjects);
                showRecyclerView();
            }
        }

    }


}
