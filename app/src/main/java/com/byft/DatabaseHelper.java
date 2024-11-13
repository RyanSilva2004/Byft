package com.byft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4; // Incremented version
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BUS = "Bus"; // New table

    // Columns for users table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";
    private static final String COLUMN_USER_TYPE = "user_type";

    // Columns for bus table
    private static final String COLUMN_BUS_NUMBER = "busNumber";
    private static final String COLUMN_BUS_OWNER_ID = "busownerID";
    private static final String COLUMN_BUS_SEATS = "busSeats";
    private static final String COLUMN_ROUTE = "route";
    private static final String COLUMN_DEPARTURE_INTERVAL = "departureInterval";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PROFILE_IMAGE + " BLOB, " +
                COLUMN_USER_TYPE + " TEXT)";
        db.execSQL(createUsersTable);

        String createBusTable = "CREATE TABLE " + TABLE_BUS + " (" +
                COLUMN_BUS_NUMBER + " VARCHAR(20) PRIMARY KEY, " +
                COLUMN_BUS_OWNER_ID + " INTEGER, " +
                COLUMN_BUS_SEATS + " INTEGER, " +
                COLUMN_ROUTE + " TEXT, " +
                COLUMN_DEPARTURE_INTERVAL + " INTEGER)";
        db.execSQL(createBusTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_TYPE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_BUS + " ADD COLUMN " + COLUMN_ROUTE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BUS + " ADD COLUMN " + COLUMN_DEPARTURE_INTERVAL + " INTEGER");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BUS + " (" +
                    COLUMN_BUS_NUMBER + " VARCHAR(20) PRIMARY KEY, " +
                    COLUMN_BUS_OWNER_ID + " INTEGER, " +
                    COLUMN_BUS_SEATS + " INTEGER, " +
                    COLUMN_ROUTE + " TEXT, " +
                    COLUMN_DEPARTURE_INTERVAL + " INTEGER)");
        }
    }

    public boolean insertUser(String name, String email, String phone, String password, @Nullable byte[] profileImage, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USER_TYPE, userType);  // Inserting user type

        if (profileImage != null) {
            values.put(COLUMN_PROFILE_IMAGE, profileImage);
        }

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUserLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            if (cursor != null) cursor.close();
            return false;
        }
    }

    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_TYPE + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE));
            cursor.close();
        }
        db.close();
        return role;
    }

    public String getUserName(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            cursor.close();
        }
        db.close();
        return name;
    }

    public byte[] getUserProfileImage(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] profileImage = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE_IMAGE + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            profileImage = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));
            cursor.close();
        }
        db.close();
        return profileImage;
    }

    public List<String> getDrivers() {
        List<String> drivers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_TYPE + " = ?", new String[]{"Bus driver"});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                drivers.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            }
            cursor.close();
        }
        db.close();
        return drivers;
    }

    public boolean insertBus(String busNumber, int busSeats, String driver, String route, int departureInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUS_NUMBER, busNumber);
        values.put(COLUMN_BUS_SEATS, busSeats);
        values.put(COLUMN_BUS_OWNER_ID, driver); // Assuming driver is the bus owner ID
        values.put(COLUMN_ROUTE, route);
        values.put(COLUMN_DEPARTURE_INTERVAL, departureInterval);

        long result = db.insert(TABLE_BUS, null, values);
        db.close();
        return result != -1;
    }
    public boolean isBusNumberExists(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUS, new String[]{COLUMN_BUS_NUMBER}, COLUMN_BUS_NUMBER + "=?", new String[]{busNumber}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}