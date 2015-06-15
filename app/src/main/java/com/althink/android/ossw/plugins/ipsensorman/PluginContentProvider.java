package com.althink.android.ossw.plugins.ipsensorman;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by krzysiek on 10/06/15.
 */
public class PluginContentProvider extends ContentProvider {

    private final static String TAG = PluginContentProvider.class.getSimpleName();

    static final String PROVIDER_NAME = "com.althink.android.ossw.plugins.ipsensorman";

    static final int PROPERTIES = 1;
    static final int FUNCTIONS = 2;
    static final int PROPERTY_ID = 3;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "properties", PROPERTIES);
        uriMatcher.addURI(PROVIDER_NAME, "functions", FUNCTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "properties/#", PROPERTY_ID);
    }


    @Override
    public boolean onCreate() {
        Log.i(TAG, "Process: " + android.os.Process.myUid());
        return true;
    }

    static final String PROPERTY_COLUMN_ID = "_id";
    static final String PROPERTY_COLUMN_NAME = "name";
    static final String PROPERTY_COLUMN_DESCRIPTION = "description";

    static final String PROPERTY_HR = "hr";
    static final String PROPERTY_CSC_SPEED = "csc_speed";
    static final String PROPERTY_CSC_CADENCE = "csc_cadence";

    private static final String[] PROPERTY_COLUMNS = new String[] {
            PROPERTY_COLUMN_ID,
            PROPERTY_COLUMN_NAME,
            PROPERTY_COLUMN_DESCRIPTION
    };

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTIES:
                MatrixCursor cursor = new MatrixCursor(PROPERTY_COLUMNS);
                addPropertyRow(cursor, PROPERTY_HR);
                addPropertyRow(cursor, PROPERTY_CSC_SPEED);
                addPropertyRow(cursor, PROPERTY_CSC_CADENCE);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private void addPropertyRow(MatrixCursor cursor, String name) {
        long id = cursor.getCount();
        cursor.newRow().add(id).add(name).add(getPropertyDescription(name));
    }

    private String getPropertyDescription(String fieldName) {
        Resources res = getContext().getResources();

        return null;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTIES:
                return "application/vnd.com.althink.android.ossw.plugin.properties";
            case FUNCTIONS:
                return "application/vnd.com.althink.android.ossw.plugin.functions";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
