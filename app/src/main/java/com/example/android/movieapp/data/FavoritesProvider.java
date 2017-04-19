package com.example.android.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

        Cursor cursor;
        int uriMatch = mUriMatcher.match(uri);
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        switch (uriMatch) {
            case FAVORITES:
            cursor = database.query(favoritesContract.favoritesEntry.TABLE_NAME,
                    projection,
                    selection,
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
