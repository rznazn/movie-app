package com.example.android.movieapp.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.android.movieapp.MovieReview;
import com.example.android.movieapp.MovieTagObject;
import com.example.android.movieapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sport on 3/11/2017.
 * for handling and translateing Json data
 */

public class MovieDBJsonUtils {

    /**
     *
     * @param jsonString the raw string data from the JSON request output
     * @return an Arraylist of the movies from the query
     * @throws JSONException
     */
    public static ArrayList<MovieTagObject> translateMoviesDBJSONToArrayList(String jsonString) throws JSONException {

        /**
         * create the arraylist to fill
         * convert the jsonstring param to JSONObject
         * and get the JSON Array "results"
         */
        ArrayList<MovieTagObject> movieTagObjects = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        /**
         * iterate through the JSON Array and convert to ArrayList
         */
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            String id = object.getString("id");
            String title = object.getString("original_title");
            String overview = object.getString("overview");
            String imagePath = object.getString("poster_path");
            String releaseDate = object.getString("release_date");
            String voterAverage = object.getString("vote_average");
            Bitmap myBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_done_black_24dp);
            byte[] byteArray = new byte[0];
            try {
                URL url  = new URL(NetworkUtils.buildImageResUri(imagePath).toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
               myBitmap = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * set extracted JSON values to the MovieTagObject
             */
            movieTagObjects.add(new MovieTagObject(id, title, overview, imagePath, releaseDate,voterAverage, byteArray));

        }
        return movieTagObjects;
    }

    /**
     * This method returns the youtube path for the trailer
     * @param idOfMovieInIMDB takes the id of the movie taken from the search result JSON
     * @return returns movie path or null upon failure.
     */
    public static String getYouTubePath(final String idOfMovieInIMDB) {

                URL url = NetworkUtils.buildTrailerUrl(idOfMovieInIMDB);
                try {
                    String videoQueryResults = NetworkUtils.getResponseFromHttpUrl(url);
                    JSONObject videosJSON = new JSONObject(videoQueryResults);
                    JSONArray videoArray =videosJSON.getJSONArray("results");
                    JSONObject firstVideoObject = videoArray.getJSONObject(0);
                    String firstVideoPath = firstVideoObject.getString("key");
                    return firstVideoPath;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

    }

    /**
     * create ArrayList from reviews JSON response
     */
    public static ArrayList<MovieReview> translateReviewsJSONToArraylist(String reviewsJSON) throws JSONException {
        ArrayList<MovieReview> reviews = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(reviewsJSON);
        if (Integer.valueOf(jsonObject.get("total_results").toString()) != 0) {
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                MovieReview movieReview;
                JSONObject reviewJSON = jsonArray.getJSONObject(i);
                String name = reviewJSON.getString("author");
                String review =  reviewJSON.getString("content").trim();
                movieReview = new MovieReview(name, review);
                reviews.add(movieReview);
            }
            return reviews;
        } else {
            return null;
        }
    }
}
