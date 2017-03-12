package com.example.android.movieapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sport on 3/11/2017.
 * Helper method for use in making MovieDB requests
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String API_KEY = "36cdde377b5c895f59c64b5af593dea7";

    private static final String MOVIE_DB_URL =
            "https://api.themoviedb.org/3/movie/";

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
                .appendQueryParameter(API_PARAM, API_KEY)
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

    public static URL buildImageResUrl(String imagePath){
        String queryUrl = "https://image.tmdb.org/t/p/w500/";
        Uri builtUri = Uri.parse(queryUrl).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
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



}
