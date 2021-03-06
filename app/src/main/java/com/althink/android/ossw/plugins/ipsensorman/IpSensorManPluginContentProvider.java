package com.althink.android.ossw.plugins.ipsensorman;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by krzysiek on 10/06/15.
 */
public class IpSensorManPluginContentProvider extends ContentProvider {

    private final static String TAG = IpSensorManPluginContentProvider.class.getSimpleName();

    static final String AUTHORITY = "com.althink.android.ossw.plugins.ipsensorman";
    static final String API_FUNCTIONS_PATH = "api/functions";
    static final String API_PROPERTIES_PATH = "api/properties";
    static final String PROVIDER_PROPERTIES = "properties";

    static final Uri PROPERTY_VALUES_URI = Uri.parse("content://" + AUTHORITY + "/" + PROVIDER_PROPERTIES);

    static final int API_PROPERTIES = 1;
    static final int API_FUNCTIONS = 2;
    static final int PROPERTIES = 3;

    private Map<String, Object> values = new HashMap<>();

    private static final String[] PROPERTY_COLUMNS;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, API_PROPERTIES_PATH, API_PROPERTIES);
        uriMatcher.addURI(AUTHORITY, API_FUNCTIONS_PATH, API_FUNCTIONS);
        uriMatcher.addURI(AUTHORITY, PROVIDER_PROPERTIES, PROPERTIES);

        LinkedList<String> properties = new LinkedList<>();
        for (IpSensorManPluginProperty property : IpSensorManPluginProperty.values()) {
            properties.add(property.getName());
        }
        PROPERTY_COLUMNS = properties.toArray(new String[properties.size()]);
    }

    @Override
    public boolean onCreate() {
        //Log.i(TAG, "Process: " + android.os.Process.myUid());
        return true;
    }

    static final String API_COLUMN_ID = "_id";
    static final String API_COLUMN_NAME = "name";
    static final String API_COLUMN_DESCRIPTION = "description";
    static final String API_COLUMN_TYPE = "type";
    static final String API_COLUMN_RANGE = "range";

    private static final String[] API_PROPERTY_COLUMNS = new String[]{
            API_COLUMN_ID,
            API_COLUMN_NAME,
            API_COLUMN_DESCRIPTION,
            API_COLUMN_TYPE,
            API_COLUMN_RANGE
    };

    private static final String[] API_FUNCTION_COLUMNS = new String[]{
            API_COLUMN_ID,
            API_COLUMN_NAME,
            API_COLUMN_DESCRIPTION
    };

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case API_PROPERTIES:
                MatrixCursor cursor = new MatrixCursor(API_PROPERTY_COLUMNS);
                addApiPropertyRow(cursor, IpSensorManPluginProperty.HEART_RATE, R.string.property_heart_rate);
                addApiPropertyRow(cursor, IpSensorManPluginProperty.CYCLING_SPEED, R.string.property_cycling_speed);
                addApiPropertyRow(cursor, IpSensorManPluginProperty.CYCLING_CADENCE, R.string.property_cycling_cadence);
                return cursor;
            case API_FUNCTIONS:
                cursor = new MatrixCursor(API_FUNCTION_COLUMNS);
                return cursor;
            case PROPERTIES:
                String[] columns = projection != null ? projection : PROPERTY_COLUMNS;
                cursor = new MatrixCursor(columns);
                MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
                for (String property : columns) {
                    addPropertyColumn(rowBuilder, property, values.get(property));
                }
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private void addPropertyColumn(MatrixCursor.RowBuilder rowBuilder, String property, Object value) {
        rowBuilder.add(value);
    }

    private void addApiPropertyRow(MatrixCursor cursor, IpSensorManPluginProperty property, int descriptionId) {
        cursor.newRow().add(property.getId()).add(property.getName()).add(getString(descriptionId)).add(property.getType().name()).add(null);
    }

    private void addApiFunctionRow(MatrixCursor cursor, IpSensorManPluginProperty function, int descriptionId) {
        cursor.newRow().add(function.getId()).add(function.getName()).add(getString(descriptionId));
    }

    private String getString(int stringId) {
        return getContext().getResources().getString(stringId);
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case API_PROPERTIES:
                return "vnd.android.cursor.dir/vnd.com.althink.android.ossw.plugin.api.properties";
            case API_FUNCTIONS:
                return "vnd.android.cursor.dir/vnd.com.althink.android.ossw.plugin.api.functions";
            case PROPERTIES:
                return "vnd.android.cursor.item/vnd.com.althink.android.ossw.plugin.properties";
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
        int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTIES:
                boolean hasChanged = false;
                for (String key : values.keySet()) {
                    IpSensorManPluginProperty property = IpSensorManPluginProperty.resolveByName(key);
                    if (property != null) {
                        Object newValue = values.get(key);
                        if (!property.getType().isTypeSupported(newValue)) {
                            throw new IllegalArgumentException("Unsupported type for property: " + property.getName());
                        }
                        Object oldValue = this.values.get(key);
                        if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                            //Log.i(TAG, "Update property '" + key + "' with value: " + newValue);
                            this.values.put(key, values.get(key));
                            hasChanged = true;
                        }
                    }
                }
                if (hasChanged) {
                    this.getContext().getContentResolver().notifyChange(uri, null);
                }
                return hasChanged ? 1 : 0;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
