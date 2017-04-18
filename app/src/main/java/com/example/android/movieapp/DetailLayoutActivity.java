package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.data.FavoritesDBHelper;
import com.example.android.movieapp.data.favoritesContract;
import com.example.android.movieapp.utils.ApiKey;
import com.example.android.movieapp.utils.MovieDBJsonUtils;
import com.example.android.movieapp.utils.NetworkUtils;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.net.URISyntaxException;

import static com.example.android.movieapp.R.string.plot;

public class DetailLayoutActivity extends AppCompatActivity {

    /**
     * member variables for the views of the detail layout
     */

    private TextView detailText;
    private ImageView detailImage;
    private ImageView playTrailerTV;
    private TextView favoriteTV;

    /**
     * variables for using youtube to play trailer.
     */
    private static final String API_KEY = ApiKey.YOUTUBE_API_KEY;
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

    /**
     * member Variable for tracking if the selected movie has been favorited
     */
    private boolean mMovieIsFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        playTrailerTV = (ImageView) findViewById(R.id.play_trailer);
        favoriteTV = (TextView) findViewById(R.id.favorite);
        detailText = (TextView) findViewById(R.id.tv_movie_detail);
        detailImage = (ImageView) findViewById(R.id.alert_dialog_imageView);
        detailImage.setAlpha(.40f);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(getString(R.string.title));
        getSupportActionBar().setTitle(mTitle);
        mID = intent.getStringExtra("id");
        mVoterAverage = intent.getStringExtra(getString(R.string.voter_average));
        mOverview = intent.getStringExtra(getString(plot));
        mReleaseDate = intent.getStringExtra(getString(R.string.release_date));
        mImagePath = intent.getStringExtra(getString(R.string.image_path));

        callCheckIfMovieIsFavoriteAsnctask(mID);
        callGetYoutubePathAsyncTask(mID);

        try {
            detailImage = MovieDBJsonUtils.loadImageFromJson(detailImage, NetworkUtils.buildImageResUri(mImagePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String overViewTextViewData = getString(R.string.release_date) + mReleaseDate
                    + "\n" + getString(R.string.voter_average) + mVoterAverage
                    + "\n" + getString(R.string.plot) + "\n"+ mOverview;

        detailText.setText(overViewTextViewData);

        /**
         * clickListener to launch movie trailer
         */
        playTrailerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerClickHandler(v);
            }
        });
        /**
         * click listener for the favorite option
         */
        favoriteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesClickHandler(v);
            }
        });


    }

    /**
     * method to check if the current movie is a favorite
     * @param mID the id of the current movie in the detail activity
     * @return true for a favorite false if not
     */
    private void callCheckIfMovieIsFavoriteAsnctask(String mID) {
        CheckIfMovieIsFavorite checkIfMovieIsFavorite = new CheckIfMovieIsFavorite();
        checkIfMovieIsFavorite.execute(mID);
    }

    /**
     * async task to handle the database query to check if the current movie is a favorite
     */
    public class CheckIfMovieIsFavorite extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            String[] args = {mID};
            FavoritesDBHelper helper = new FavoritesDBHelper(DetailLayoutActivity.this);
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(favoritesContract.favoritesEntry.TABLE_NAME,
                    null,
                    favoritesContract.favoritesEntry.COLUMN_MOVIE_ID + " = ? ",
                    args,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()){
                cursor.close();
                return true;
            } else{
                cursor.close();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mMovieIsFavorite = aBoolean;
            setFavoriteView(aBoolean);
        }
    }

    private void setFavoriteView(boolean trueToFavorite){
        if (trueToFavorite) {
            favoriteTV.setBackgroundColor(getResources().getColor(R.color.colorForFavorite));
        } else if (!trueToFavorite){
            favoriteTV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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
                        DetailLayoutActivity.this, API_KEY, mYoutubePath, 0, true, true);
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
     * initial disposition determined for click event
     * @param v
     */
    private void favoritesClickHandler(View v){
        if (!mMovieIsFavorite){
            AddMovieToFavorites addMovieToFavorites = new AddMovieToFavorites();
            addMovieToFavorites.execute();
        } else if (mMovieIsFavorite){
            RemoveMovieFromFavorites removeMovieFromFavorites = new RemoveMovieFromFavorites();
            removeMovieFromFavorites.execute();
        }

    }

    /**
     * Async task to add movie to favorites
     */
    public class AddMovieToFavorites extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            FavoritesDBHelper helper = new FavoritesDBHelper(DetailLayoutActivity.this);
            SQLiteDatabase database = helper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_ID, mID);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME, mTitle);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW, mOverview);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH, mImagePath);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
            contentValues.put(favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE, mVoterAverage);

            long rowid = database.insert(favoritesContract.favoritesEntry.TABLE_NAME, null, contentValues);

            if (rowid >0 ){
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(DetailLayoutActivity.this, "Movie added to favorites", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DetailLayoutActivity.this, "failed to add movie", Toast.LENGTH_LONG).show();

            }
            mMovieIsFavorite = aBoolean;
            setFavoriteView(mMovieIsFavorite);
        }
    }

    /**
     * Async task to remove movie from favorites
     */
    public class RemoveMovieFromFavorites extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            String[] args = {mID};
            FavoritesDBHelper helper = new FavoritesDBHelper(DetailLayoutActivity.this);
            SQLiteDatabase database = helper.getWritableDatabase();

            int rowsDeleted = database.delete(favoritesContract.favoritesEntry.TABLE_NAME,
                    favoritesContract.favoritesEntry.COLUMN_MOVIE_ID + " = ? ",
                    args);

            if (rowsDeleted >0 ){
                return true;
            } else {
                return false;
            }
        }

        /**
         * @param aBoolean if true, indicates that movie was successfully deleted from favorites
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                mMovieIsFavorite = false;
                setFavoriteView(mMovieIsFavorite);
                Toast.makeText(DetailLayoutActivity.this, "Movie deleted from favorites", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DetailLayoutActivity.this, "failed to remove movie from favorites", Toast.LENGTH_LONG).show();

            }

        }
    }

}
