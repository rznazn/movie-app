package com.example.android.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.R.attr.id;

/**
 * Created by sport on 4/18/2017.
 */

public class FavoritesProvider extends ContentProvider {

    /**
     * member variables
     */
    private FavoritesDBHelper mDBHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    /**
     * build the uri matcher for this provider
     * @return
     */
    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher( UriMatcher.NO_MATCH);

        uriMatcher.addURI(favoritesContract.CONTENT_AUTHORITY, favoritesContract.PATH_NAME, FAVORITES);
        uriMatcher.addURI(favoritesContract.CONTENT_AUTHORITY, favoritesContract.PATH_NAME + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    /**
     * instantiate dbhelper
     * @return true for completed
     */
    @Override
    public boolean onCreate() {
        mDBHelper = new FavoritesDBHelper(getContext());
        return true;
    }

    /**
     * Query the database
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

         String selectionStatement = null;
        if (selection != null && selection != ""){
            selectionStatement = selection + " = ? ";
        }
        Cursor cursor;
        int uriMatch = mUriMatcher.match(uri);
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        switch (uriMatch) {
            case FAVORITES:
            cursor = database.query(favoritesContract.favoritesEntry.TABLE_NAME,
                    projection,
                    selectionStatement,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * method used to add new movie to favorites database
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri uriToReturn;
        int uriMatch = mUriMatcher.match(uri);
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        switch (uriMatch) {
            case FAVORITES:
                long rowId = database.insert(favoritesContract.favoritesEntry.TABLE_NAME,null, values);
                if (id > 0) {
                    uriToReturn = ContentUris.withAppendedId(favoritesContract.favoritesEntry.CONTENT_URI, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return uriToReturn;

    }

    /**
     * method will remove a movie from favorites list
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        int uriMatch = mUriMatcher.match(uri);
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        switch (uriMatch) {
            case FAVORITES:
                rowsDeleted = database.delete(favoritesContract.favoritesEntry.TABLE_NAME,selection + "= ? ",selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsDeleted;
    }

    /**
     * method will not be used.
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
