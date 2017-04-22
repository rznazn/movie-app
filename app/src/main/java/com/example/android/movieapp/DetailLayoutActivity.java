package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.data.favoritesContract;
import com.example.android.movieapp.utils.ApiKey;
import com.example.android.movieapp.utils.MovieDBJsonUtils;
import com.example.android.movieapp.utils.MovieReviewRecyclerViewAdapter;
import com.example.android.movieapp.utils.NetworkUtils;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

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
    private ImageView shareIV;
    private RecyclerView reviewsRV;

    /**
     * variables for using youtube to play trailer.
     */
    private static final String YOUTUBE_API_KEY = ApiKey.YOUTUBE_API_KEY;
    private String mYoutubePath;
    private String mYoutubeURL;

    /**
     * member variables for the movie object parts
     */
    private MovieTagObject mCurrentMovie;
    private String mID;
    private String mTitle;
    private String mOverview;
    private String mImagePath;
    private String mReleaseDate;
    private String mVoterAverage;
    private String mReviews;
    private byte[] mPoster;
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
        releaseDateTV = (TextView) findViewById(R.id.release_date);
        voterAverageTV = (TextView) findViewById(R.id.voter_average);
        scrollViewHeaderTV = (TextView) findViewById(R.id.scrollview_header);
        reviewsIV = (ImageView) findViewById(R.id.reviews);
        shareIV = (ImageView) findViewById(R.id.share_trailer);
        reviewsRV = (RecyclerView) findViewById(R.id.review_rv);

        /**
         * instate the recyclerVIew and set layoutManager and custom adapter
         */
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsRV.setLayoutManager(manager);
        movieReviewRecyclerViewAdapter = new MovieReviewRecyclerViewAdapter();
        reviewsRV.setAdapter(movieReviewRecyclerViewAdapter);


        posterIV = (ImageView) findViewById(R.id.alert_dialog_imageView);
        posterIV.setAlpha(.40f);

        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        int movieArrayId = intentBundle.getInt("id");
        int movieID = intentBundle.getInt("movieId");
        if (preferences.sortPreference == MainActivity.HIGH_RATED) {
            mCurrentMovie = preferences.mHighestRatedMovies.get(movieArrayId);
        } else if (preferences.sortPreference == MainActivity.MOST_POPULAR) {
            mCurrentMovie = preferences.mPopularMovies.get(movieArrayId);
        } else if (preferences.sortPreference == MainActivity.FAVORITE) {
            String[] args = {String.valueOf(movieID)};
            Cursor currentMovieCursur = getContentResolver().query(favoritesContract.favoritesEntry.CONTENT_URI,
                    null,
                    favoritesContract.favoritesEntry.COLUMN_MOVIE_ID,
                    args,
                    null);
            if (currentMovieCursur.moveToFirst()) {
                String id = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_ID));
                String title = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME));
                String overview = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW));
                String posterPath = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH));
                String voterAverage = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE));
                String releaseDate = currentMovieCursur.getString(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE));
                byte[] posterImage = currentMovieCursur.getBlob(currentMovieCursur.
                        getColumnIndex(favoritesContract.favoritesEntry.COLUMN_MOVIE_POSTER_BLOB));
                mCurrentMovie = new MovieTagObject(id, title, overview, posterPath, releaseDate, voterAverage, posterImage);
            }
        }
        mID = mCurrentMovie.getId();
        mTitle = mCurrentMovie.getTitle();
        getSupportActionBar().setTitle(mTitle);
        mOverview = mCurrentMovie.getOverview();
        mImagePath = mCurrentMovie.getImagePath();
        mVoterAverage = mCurrentMovie.getVoterAverage();
        mReleaseDate = mCurrentMovie.getReleaseDate();
        /***
         * check if movie is already a favorite
         */
        checkIfMovieIsFavorite();

        /**
         * get and stage the path for the youtube trailer
         */
        callGetYoutubePathAsyncTask(mID);
        /**
         * get the movie's reviews and stage in recyclerVIew
         */
        callGetReviewsAsyncTask(mID);

        /**
         * load poster to background
         */
        mPoster = mCurrentMovie.getPosterImage();
        Bitmap posterBitmap = BitmapFactory.decodeByteArray(mPoster,
                0,
                mPoster.length);
        posterIV.setImageBitmap(posterBitmap);

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
        /**
         * clickListener to share the youtube trailer url
         */
        shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(DetailLayoutActivity.this)
                        .setChooserTitle("share youtube trailer")
                        .setType("text/plain")
                        .setText(mYoutubeURL)
                        .startChooser();
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
     * check connectivity and adjust visible view prior to starting the async task
     * show error screen if there is no connectivity
     */
    private void callGetReviewsAsyncTask(String movieId){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            new GetMoviesReviewsTask().execute(movieId);

        } else {
            MovieReview noReviewWithNoNet = new MovieReview(getString(R.string.no_internet),
                    getString(R.string.no_reviews));
            mMovieReviews.add(noReviewWithNoNet);
            movieReviewRecyclerViewAdapter.setmMovieReviews(mMovieReviews);
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
            mYoutubeURL = "https://www.youtube.com/watch?v=" + s;
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
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_POSTER_BLOB, mPoster);
            Uri rowUri = getContentResolver().insert(favoritesContract.favoritesEntry.CONTENT_URI, contentValues);

            if (rowUri != null ){
                mMovieIsFavorite = true;
            } else {
                mMovieIsFavorite = false;
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

    /**
     * show the reviews window and hide plot
     */
    private void showReviews(){
        detailTV.setVisibility(View.GONE);
        reviewsRV.setVisibility(View.VISIBLE);
    }

    /**
     * show plot textView and hide the reviews
     */
    private void showPlot(){
        detailTV.setVisibility(View.VISIBLE);
        reviewsRV.setVisibility(View.GONE);
    }

}
