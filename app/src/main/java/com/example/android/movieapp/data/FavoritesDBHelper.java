package com.example.android.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sport on 4/16/2017.
 */

public class FavoritesDBHelper extends SQLiteOpenHelper {

    /**
     * database variables
     */
    private static final String DATABASE_NAME = "movieTables.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + favoritesContract.favoritesEntry.TABLE_NAME + " (" +
                favoritesContract.favoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_IMAGE_PATH + " TEXT NOT NULL, " +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                favoritesContract.favoritesEntry.COLUMN_MOVIE_VOTER_AVERAGE + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
