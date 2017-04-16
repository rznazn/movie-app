package com.example.android.movieapp.favoritesSqlDB;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sport on 4/16/2017.
 */

public class favoritesContract {
    /**
     * URI component strings
     * path is not specified locally as this app is intended to creat multiple tables.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_NAME = "movieTables";
    public static String mTableName = "favorites";

    /**
     * private constructor to prevent outside creation
     */
    private favoritesContract(){}

    /**
     * Inner class that defines the Constants for the list database columns
     */
    public static final class ListEntry implements BaseColumns {
        /**
         * The MIME type variables
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NAME;

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NAME);

        /**
         * Set the String Constants to represent the column names
         */
        public static final String TABLE_NAME = mTableName;
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_NAME = "title";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_IMAGE_PATH = "imagePath";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_VOTER_AVERAGE = "voterAverage";

    }
}
