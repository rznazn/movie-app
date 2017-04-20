package com.example.android.movieapp.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sport on 3/11/2017.
 * Helper method for use in making MovieDB requests
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * value for API KEY is stored in a class not inculded in git
     * in order to practice keeping API KEYS confidential
     */
    private static final String MOVIEDB_API_KEY = ApiKey.MOVIEDB_API_KEY;

    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/";

    private static final String MOVIE_BASE_URL = MOVIE_DB_URL;

    private static final String LANGUAGE_PARAM = "language";

    private static final String LANGUAGE_PREFERENCE = "en";

    /**
     *
     */
    final static String API_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movieDB.
     *
     * @param sortQuery The way in which to search the results
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String sortQuery) {

        String queryUrl = MOVIE_BASE_URL + sortQuery + '?';
        Uri builtUri = Uri.parse(queryUrl).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIEDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_PREFERENCE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Builds the URL used to talk to the movieDB.
     *
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buildTrailerUrl(String movieId) {

        String queryUrl = MOVIE_BASE_URL + movieId + "/videos";
        Uri builtUri = Uri.parse(queryUrl).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIEDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_PREFERENCE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return url;
    }

    /**
     * take the image path and returns the uri to get the movie poster
     * @param imagePath
     * @return
     * @throws URISyntaxException
     */
    public static Uri buildImageResUri(String imagePath) throws URISyntaxException {
        String queryUrl = "https://image.tmdb.org/t/p/w500" + imagePath;
        Uri builtUri = Uri.parse(queryUrl).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIEDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_PREFERENCE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Uri imageUri = Uri.parse(queryUrl);
        return imageUri;
    }

    /**
     * This method returns the json result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * builds the URL for the reviews
     * @param movieId
     * @return
     */
    public static  URL buildReviewsUri(String movieId){

        String queryUrl = MOVIE_BASE_URL + movieId + "/reviews";
        Uri builtUri = Uri.parse(queryUrl).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIEDB_API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_PREFERENCE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }



}
