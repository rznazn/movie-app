package com.example.android.movieapp.utils;

import android.net.Uri;
import android.widget.ImageView;

import com.example.android.movieapp.MovieTagObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static ArrayList<MovieTagObject> translateStringToArrayList(String jsonString) throws JSONException {

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

            /**
             * set extracted JSON values to the MovieTagObject
             */
            movieTagObjects.add(new MovieTagObject(id, title, overview, imagePath, releaseDate,voterAverage));

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
     * @param view ImageView that the image should be assigned to, this allows the desired image to
     *             be passed in with all other values assigned to it.
     * @param uri
     * @return the same Image View as was passed in except loaded with the image from the Uri
     */
    public static ImageView loadImageFromJson(ImageView view, Uri uri){
        Picasso.with(view.getContext()).load(uri).into(view);
        return view;
    }
}
