package com.example.android.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.android.movieapp.data.FavoritesDBHelper;
import com.example.android.movieapp.data.favoritesContract;
import com.example.android.movieapp.utils.MovieAdapter;
import com.example.android.movieapp.utils.MovieDBJsonUtils;
import com.example.android.movieapp.utils.NetworkUtils;

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
    private static final String FAVORITE = "favorite";
    private static final String HIGH_RATED = "high rated";
    private static final String MOST_POPULAR = "most popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(sortPreference);


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
        callAsyncTask();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mMovieAdapter = new MovieAdapter(this, mMovieTagObjects);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (sortPreference){
            case FAVORITE:
                loadFavoritesFromDatabase();
                break;
            case HIGH_RATED:
                callAsyncTask();
                break;
            case MOST_POPULAR:
                callAsyncTask();
                break;
        }
    }

    /**
     * three "SHOW" methods to show the desired screen
     */
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
                sortPreference = MOST_POPULAR;
                getSupportActionBar().setTitle(R.string.popular);
                callAsyncTask();
                break;
            case R.id.action_sort_by_highest_rated:
                sortPreference = HIGH_RATED;
                getSupportActionBar().setTitle(R.string.highest_rated);
                callAsyncTask();
                break;
            case R.id.action_show_favorites:
                sortPreference = FAVORITE;
                loadFavoritesFromDatabase();
                getSupportActionBar().setTitle(R.string.favorite);
                break;
        }
        return true;
    }

    /**
     * this method takes the content from the favorites database and sends it to the adapter
     */
    private void loadFavoritesFromDatabase() {
        mMovieTagObjects.clear();
        FavoritesDBHelper helper = new FavoritesDBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(favoritesContract.favoritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME));
            String overview = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW));
            String imagePath = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE));
            String voterAverage = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE));


            mMovieTagObjects.add(new MovieTagObject(id, title, overview, imagePath, releaseDate,voterAverage));
        }
        cursor.close();
        mMovieAdapter.setMovieData(mMovieTagObjects);

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

    /**
     * check connectivity and adjust visible view prior to starting the async task
     * show error screen if there is no connectivity
     */
    private void callAsyncTask (){
        showProgressBar();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            new GetMoviesTask().execute(sortPreference);

        }else {
            showErrorScreen();
        }
    }


}
