package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.data.favoritesContract;
import com.example.android.movieapp.utils.ApiKey;
import com.example.android.movieapp.utils.MovieDBJsonUtils;
import com.example.android.movieapp.utils.MovieReviewRecyclerViewAdapter;
import com.example.android.movieapp.utils.NetworkUtils;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.movieapp.R.string.plot;

public class DetailLayoutActivity extends AppCompatActivity {

    /**
     * member variables for the views of the detail layout
     */

    private TextView detailTV;
    private TextView releaseDateTV;
    private TextView voterAverageTV;
    private TextView scrollViewHeaderTV;
    private ImageView posterIV;
    private ImageView playTrailerIV;
    private ImageView favoriteIV;
    private ImageView reviewsIV;
    private RecyclerView reviewsRV;

    /**
     * variables for using youtube to play trailer.
     */
    private static final String YOUTUBE_API_KEY = ApiKey.YOUTUBE_API_KEY;
    private String mYoutubePath;

    /**
     * member variables for the movie object parts
     */
    private String mID;
    private String mTitle;
    private String mOverview;
    private String mImagePath;
    private String mReleaseDate;
    private String mVoterAverage;
    private String mReviews;
    private ArrayList<MovieReview> mMovieReviews = new ArrayList<>();
    private MovieReviewRecyclerViewAdapter movieReviewRecyclerViewAdapter;

    /**
     * member Variable for tracking if the selected movie has been favorited
     */
    private boolean mMovieIsFavorite;
    private boolean mOverviewDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        playTrailerIV = (ImageView) findViewById(R.id.play_trailer);
        favoriteIV = (ImageView) findViewById(R.id.favorite);
        detailTV = (TextView) findViewById(R.id.tv_movie_detail);
        releaseDateTV = (TextView)findViewById(R.id.release_date);
        voterAverageTV = (TextView) findViewById(R.id.voter_average);
        scrollViewHeaderTV = (TextView) findViewById(R.id.scrollview_header);
        reviewsIV = (ImageView) findViewById(R.id.reviews);
        reviewsRV = (RecyclerView) findViewById(R.id.review_rv);

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        reviewsRV.setLayoutManager(manager);
        movieReviewRecyclerViewAdapter = new MovieReviewRecyclerViewAdapter();
        reviewsRV.setAdapter(movieReviewRecyclerViewAdapter);


        posterIV = (ImageView) findViewById(R.id.alert_dialog_imageView);
        posterIV.setAlpha(.40f);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(getString(R.string.title));
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mID = intent.getStringExtra("id");
        mVoterAverage = intent.getStringExtra(getString(R.string.voter_average));
        mOverview =  intent.getStringExtra(getString(plot));
        mReleaseDate = intent.getStringExtra(getString(R.string.release_date));
        mImagePath = intent.getStringExtra(getString(R.string.image_path));

        checkIfMovieIsFavorite();
        callGetYoutubePathAsyncTask(mID);

        try {
            posterIV = MovieDBJsonUtils.loadImageFromJson(posterIV, NetworkUtils.buildImageResUri(mImagePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        new GetMoviesReviewsTask().execute(mID);

        detailTV.setText(mOverview);
        showPlot();
        releaseDateTV.setText(mReleaseDate);
        voterAverageTV.setText(mVoterAverage);

        /**
         * clickListener to launch movie trailer
         */
        playTrailerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerClickHandler(v);
            }
        });
        /**
         * click listener for the favorite option
         */
        favoriteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesClickHandler(v);
            }
        });
        /**
         * clicklisenter for the reviews button
         */
        reviewsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewsClickHandler(v);
            }
        });

    }

    /**
     * method for checking if the current movie is a favorite
     */

    private void checkIfMovieIsFavorite() {
        String[] args = {mID};
        Cursor cursor = getContentResolver().query(favoritesContract.favoritesEntry.CONTENT_URI,
                null,
                favoritesContract.favoritesEntry.COLUMN_MOVIE_ID,
                args,
                null);
        mMovieIsFavorite = cursor.moveToFirst();
        cursor.close();
        setFavoriteView(mMovieIsFavorite);

    }


    /**
     * @param trueToFavorite uses param to determine if the icon should reflect currently favorite or not
     */
    private void setFavoriteView(boolean trueToFavorite){
        if (trueToFavorite) {
            favoriteIV.setBackground(getResources().getDrawable(R.drawable.yellowstar));
        } else if (!trueToFavorite){
            favoriteIV.setBackground(getResources().getDrawable(R.drawable.clearstar));
        }
    }

    /**
     * check connectivity and adjust visible view prior to starting the async task
     * show error screen if there is no connectivity
     */
    private void callGetYoutubePathAsyncTask(String movieId){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            new GetYoutubePath().execute(movieId);

        }
    }

    /**
     * AsyncTask used to get the youtube path for the trailer
     */
    public class GetYoutubePath extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            return MovieDBJsonUtils.getYouTubePath(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mYoutubePath = s;

        }
    }

    /**
     * handle the click selection to play trailer
     * @param v
     */
    private void trailerClickHandler(View v){

        if (mYoutubePath != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        DetailLayoutActivity.this, YOUTUBE_API_KEY, mYoutubePath, 0, true, true);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailLayoutActivity.this);
                builder.setMessage(R.string.youtube_error_message);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailLayoutActivity.this);
            builder.setMessage(R.string.no_trailer);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**
     * Favorite or unfavorite movie based on boolean mMovieIsFavorite
     * @param v
     */
    private void favoritesClickHandler(View v){
        /**
         * if mMovieIsFavorite is false, add to favorites and set mMovieIsFavorite as true
         */
        if (!mMovieIsFavorite){
            ContentValues contentValues = new ContentValues();
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_ID, mID);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME, mTitle);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW, mOverview);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH, mImagePath);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE, mVoterAverage);

            Uri rowUri = getContentResolver().insert(favoritesContract.favoritesEntry.CONTENT_URI, contentValues);

            if (rowUri != null ){
                mMovieIsFavorite = true;
                Toast.makeText(DetailLayoutActivity.this, "Movie added to favorites", Toast.LENGTH_LONG).show();
            } else {
                mMovieIsFavorite = false;
                Toast.makeText(DetailLayoutActivity.this, "failed to add movie", Toast.LENGTH_LONG).show();
            }
            setFavoriteView(mMovieIsFavorite);
        } else if (mMovieIsFavorite){
            String[] args = {mID};

            int rowsDeleted = getContentResolver().delete(favoritesContract.favoritesEntry.CONTENT_URI,
                    favoritesContract.favoritesEntry.COLUMN_MOVIE_ID,
                    args);

            if (rowsDeleted >0 ){
                mMovieIsFavorite = false;
                setFavoriteView(mMovieIsFavorite);
                Toast.makeText(DetailLayoutActivity.this, "Movie deleted from favorites", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(DetailLayoutActivity.this, "failed to remove movie from favorites", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * reviews click handler
     */
    private void reviewsClickHandler(View v){
        if (mOverviewDisplayed){
            scrollViewHeaderTV.setText(R.string.reviews);
            mOverviewDisplayed = false;
            showReviews();
        }else if(!mOverviewDisplayed){
            scrollViewHeaderTV.setText(R.string.plot);
            mOverviewDisplayed = true;
            showPlot();
        }
    }
    /**
     * Async Task the makes network request to get movie reviews
     */
    public class GetMoviesReviewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            URL movieUrl = NetworkUtils.buildReviewsUri(movieId);

            try {
                String responseFromHttpUrl = NetworkUtils
                        .getResponseFromHttpUrl(movieUrl);

                return responseFromHttpUrl;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
                try {
                    ArrayList<MovieReview> response = MovieDBJsonUtils.translateReviewsJSONToArraylist(s);
                    if (response != null){
                        mMovieReviews = response;
                        movieReviewRecyclerViewAdapter.setmMovieReviews(mMovieReviews);
                    } else {
                        mReviews = getResources().getString(R.string.no_reviews);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    private void showReviews(){
        detailTV.setVisibility(View.GONE);
        reviewsRV.setVisibility(View.VISIBLE);
    }

    private void showPlot(){
        detailTV.setVisibility(View.VISIBLE);
        reviewsRV.setVisibility(View.GONE);
    }

}
