package com.example.locationpinnedapp.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.locationpinnedapp.model.LocationModel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LOCATION= "location";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS= "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    // SQL query to create the Location table
    private static final String CREATE_TABLE_Location =
            "CREATE TABLE " + TABLE_LOCATION + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_LATITUDE + " TEXT," +
                    COLUMN_LONGITUDE + " TEXT" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the location table
        db.execSQL(CREATE_TABLE_Location);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dropping the existing location table and creating a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

    // Method to add a new location to the database
    public void addLocation(LocationModel locationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, locationModel.getAddress());
        values.put(COLUMN_LATITUDE, locationModel.getLatitude());
        values.put(COLUMN_LONGITUDE, locationModel.getLongitude());
        db.insert(TABLE_LOCATION, null, values);
        db.close();
    }

    // Method to retrieve all location from the database
    public List<LocationModel> getAllLocations() {
        List<LocationModel> locationList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setId(cursor.getString(0));
                locationModel.setAddress(cursor.getString(1));
                locationModel.setLatitude(cursor.getString(2));
                locationModel.setLongitude(cursor.getString(3));

                locationList.add(locationModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return locationList;
    }

    // Method to retrieve location with a specific address from the database
    public List<LocationModel> getLocationByAddress(String location) {
        List<LocationModel> locationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_ADDRESS, COLUMN_LATITUDE, COLUMN_LONGITUDE};
        String selection = COLUMN_ADDRESS + " LIKE ?";
        String[] selectionArgs = {"%" + location.toLowerCase() + "%"};

        Cursor cursor = db.query(TABLE_LOCATION, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setId(cursor.getString(0));
                locationModel.setAddress(cursor.getString(1));
                locationModel.setLatitude(cursor.getString(2));
                locationModel.setLongitude(cursor.getString(3));

                locationList.add(locationModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return locationList;
    }

    public int updateLocation(LocationModel locationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, locationModel.getAddress());
        values.put(COLUMN_LATITUDE, locationModel.getLatitude());
        values.put(COLUMN_LONGITUDE, locationModel.getLongitude());

        // Updating row
        return db.update(TABLE_LOCATION, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(locationModel.getId())});
    }

    public void deleteLocation(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }


}
