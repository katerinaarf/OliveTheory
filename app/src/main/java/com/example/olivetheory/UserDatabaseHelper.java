package com.example.olivetheory;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final String TABLE_NAME = "user_table";
    private static final String TABLE_LOCATIONS = "location_table";
    // User Table Columns
    private static final String KEY_ID = "ID";
    private static final String KEY_USERNAME = "NAME";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_PASSWORD = "PASSWORD";
    private static final String KEY_USER_TYPE = "USER_TYPE";

    // Place Table Columns
    private static final String KEY_LOCATION_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_USER_ID_FK = "user_id";


    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID  + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USERNAME  + "TEXT," +
                KEY_EMAIL + "TEXT," +
                KEY_PASSWORD + "TEXT," +
                KEY_USER_TYPE + "TEXT" +
                ")");

        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_LOCATION_ID + " INTEGER PRIMARY KEY,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL,"
                + KEY_USER_ID_FK + " INTEGER,"
                + " FOREIGN KEY (" + KEY_USER_ID_FK + ") REFERENCES " + TABLE_LOCATIONS + "(" + KEY_LOCATION_ID + ")"
                + ")";
        db.execSQL(CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }
    // CRUD operations for User
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, user.getName());
        contentValues.put(KEY_EMAIL, user.getEmail());
        contentValues.put(KEY_PASSWORD, user.getPassword());
        contentValues.put(KEY_USER_TYPE, user.getUserType());

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
            if (cursor != null && cursor.getCount() > 0) {
                return true; // User exists
            }
            return false; // User does not exist
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }




    public String getUserType(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT userType FROM users WHERE email = ?", new String[]{email});
        String userType = "";
        if (cursor.moveToFirst()) {
            userType = cursor.getString(0);
        }
        cursor.close();
        return userType;
    }


    // CRUD operations for Place
    public void addLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, location.getLatitude());
        values.put(KEY_LONGITUDE, location.getLongitude());
        values.put(KEY_USER_ID_FK, location.getUserId());

        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }

    public List<Location> getLocationsByUserId(int userId) {
        List<Location> placeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + KEY_USER_ID_FK + " = " + userId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Location place = new Location(
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USER_ID_FK))
                );
                placeList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return placeList;
    }


    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_2 + " FROM " + TABLE_NAME + " WHERE " + COL_3 + "=?", new String[]{email});
        String userName = null;
        if (cursor.moveToFirst()) {
            userName = cursor.getString(0);
        }
        cursor.close();
        return userName;
    }



}
