package com.example.android.movieapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movieapp.data.favoritesContract;
import com.example.android.movieapp.utils.MovieAdapter;
import com.example.android.movieapp.utils.MovieDBJsonUtils;
import com.example.android.movieapp.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.movieapp.preferences.sortPreference;

/**
 * Application Main Activity
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<MovieTagObject>>{


    /**
     * Variables for use in handling the UI layouts/views/layout manager/adapter
     */
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView mErrorScreen;
    private ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;
    private int spanCount = 2;
    private ArrayList<MovieTagObject> mMovieTagObjects = new ArrayList<>();

    /**
     * Variable to be used in determining the sort order for the search
     */
    public static String response = "failed";
    public static final String FAVORITE = "favorite";
    public static final String HIGH_RATED = "top_rated";
    public static final String MOST_POPULAR = "popular";

    /**
     * Loader variables
     */

    private LoaderManager mLoaderManager;
    private Loader mLoader;
    private static final int POPULAR_LOADER_ID = 1;
    private static final int HIGHEST_RATED_LOADER_ID = 2;
    private static final String SEARCH_PREFERENCE= "search preference";

    Bundle highRatedBundle = new Bundle();
    Bundle popularBundle = new Bundle();

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
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mMovieAdapter = new MovieAdapter(this, mMovieTagObjects);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoaderManager = getSupportLoaderManager();

        mLoader = mLoaderManager.getLoader(POPULAR_LOADER_ID);
        popularBundle.putString(SEARCH_PREFERENCE, MOST_POPULAR);
        mLoaderManager.initLoader(POPULAR_LOADER_ID,popularBundle,this);

        mLoader = mLoaderManager.getLoader(HIGHEST_RATED_LOADER_ID);
        highRatedBundle.putString(SEARCH_PREFERENCE, HIGH_RATED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (sortPreference){
            case FAVORITE:
                loadFavoritesFromDatabase();
                break;
            case HIGH_RATED:
                showProgressBar();
                mLoaderManager.restartLoader(HIGHEST_RATED_LOADER_ID,highRatedBundle,this);
                break;
            case MOST_POPULAR:
                showProgressBar();
                mLoaderManager.restartLoader(POPULAR_LOADER_ID,popularBundle,this);
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
                mMovieAdapter.setMovieData(preferences.mPopularMovies);
                mLoaderManager.restartLoader(POPULAR_LOADER_ID,popularBundle,this);
                getSupportActionBar().setTitle(R.string.popular);
                break;
            case R.id.action_sort_by_highest_rated:
                sortPreference = HIGH_RATED;
                mLoaderManager.restartLoader(HIGHEST_RATED_LOADER_ID,highRatedBundle,this);
                getSupportActionBar().setTitle(R.string.highest_rated);
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
        Cursor cursor = getContentResolver().query(favoritesContract.favoritesEntry.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME));
            String overview = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW));
            String imagePath = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE));
            String voterAverage = cursor.getString(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE));
            byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_POSTER_BLOB));

            mMovieTagObjects.add(new MovieTagObject(id, title, overview, imagePath, releaseDate,voterAverage, byteArray));
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

    @Override
    public Loader<ArrayList<MovieTagObject>> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<MovieTagObject>>(this) {

            @Override
            protected void onStartLoading() {
                showProgressBar();
                if (!preferences.mPopularMovies.isEmpty() && sortPreference == MOST_POPULAR) {
                    Log.v("main activity ", "net request saved");
                    deliverResult(preferences.mPopularMovies);
                }else  if (!preferences.mHighestRatedMovies.isEmpty() && sortPreference == HIGH_RATED) {
                    Log.v("main activity ", "net request saved");
                    deliverResult(preferences.mHighestRatedMovies);
                }else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<MovieTagObject> loadInBackground() {
                ArrayList<MovieTagObject> movieTagObjects = new ArrayList<>();
                if (args.isEmpty()) {
                    return null;
                }

                String searchBy = args.getString(SEARCH_PREFERENCE);
                URL movieUrl = NetworkUtils.buildUrl(searchBy);

                try {
                    String response = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                    movieTagObjects = MovieDBJsonUtils.translateMoviesDBJSONToArrayList(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return movieTagObjects;


            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieTagObject>> loader, ArrayList<MovieTagObject> data) {
        if (data == null){
            showErrorScreen();
        }else if (loader.getId() == POPULAR_LOADER_ID){
            preferences.mPopularMovies = data;
        }else if (loader.getId() == HIGHEST_RATED_LOADER_ID){
            preferences.mHighestRatedMovies = data;
        }
            mMovieAdapter.setMovieData(data);
            showRecyclerView();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieTagObject>> loader) {

    }

}
