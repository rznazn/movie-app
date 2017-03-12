package com.example.android.movieapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

/**
 * Application Main Activity
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    /**
     * Variables for use in handling the UI layouts/views/layout manager/adapter
     */
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int spanCount = 2;
    private  ArrayList<MovieTagObject> mMovieTagObjects = null;

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
         * instantiate variables and assign layout manager and adapter.
         */
        new GetMoviesTask().execute(sortPreference);
        mLayoutManager = new GridLayoutManager(this, spanCount
        , GridLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mMovieAdapter = new MovieAdapter(this,mMovieTagObjects);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    /**
     * inflate options menu with the options for sorting by popularity or ranking
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected){
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
            try {
                mMovieTagObjects = MovieDBJsonUtils.translateStringToArrayList(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mMovieAdapter.setMovieData(mMovieTagObjects);
        }
    }


}
