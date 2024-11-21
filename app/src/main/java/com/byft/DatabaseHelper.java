package com.byft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 10; // Incremented version
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BUS = "Bus";
    private static final String TABLE_BUS_SCHEDULE = "BusSchedule";
    private static final String TABLE_BOOKINGS = "Bookings";
    private static final String TABLE_RATINGS = "Ratings"; // New table
    private static final String TABLE_CANCEL_BOOKINGS = "CancelBookings";

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
    private static final String COLUMN_DEPARTURE_INTERVAL = "departureInterval";
    private static final String COLUMN_DRIVER = "driver";

    // Columns for bus schedule table
    private static final String COLUMN_SCHEDULE_ID = "scheduleID";
    private static final String COLUMN_SCHEDULE_BUS_NUMBER = "busNumber";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_TRIP_TIME = "tripTime";
    private static final String COLUMN_TRIP_DIRECTION = "tripDirection";
    private static final String COLUMN_START_LOCATION = "startLocation";
    private static final String COLUMN_END_LOCATION = "endLocation";

    // Columns for bookings table
    private static final String COLUMN_BOOKING_ID = "bookingID";
    private static final String COLUMN_BOOKING_SCHEDULE_ID = "scheduleID";
    private static final String COLUMN_BOOKING_BUS_NUMBER = "busNumber";
    private static final String COLUMN_SEAT_NUMBER = "seatNumber";
    private static final String COLUMN_BOOKING_USER_ID = "userID";

    // Columns for ratings table
    private static final String COLUMN_RATING_ID = "ratingID";
    private static final String COLUMN_RATING_USER_EMAIL = "userEmail";
    private static final String COLUMN_RATING_BUS_NUMBER = "busNumber";
    private static final String COLUMN_RATING_VALUE = "ratingValue";

    // Columns for cancel bookings table
    private static final String COLUMN_CANCEL_ID = "cancelID";
    private static final String COLUMN_CANCEL_BOOKING_ID = "bookingID";
    private static final String COLUMN_CANCEL_BUS_NUMBER = "busNumber";
    private static final String COLUMN_CANCEL_SEAT_NUMBER = "seatNumber";
    private static final String COLUMN_CANCEL_USER_EMAIL = "userEmail";
    private static final String COLUMN_CANCEL_STATE = "state";

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
                COLUMN_DEPARTURE_INTERVAL + " INTEGER, " +
                COLUMN_DRIVER + " TEXT)"; // Add the driver column
        db.execSQL(createBusTable);

        String createBusScheduleTable = "CREATE TABLE " + TABLE_BUS_SCHEDULE + " (" +
                COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCHEDULE_BUS_NUMBER + " VARCHAR(20), " +
                COLUMN_DAY + " TEXT, " +
                COLUMN_TRIP_TIME + " TEXT, " +
                COLUMN_TRIP_DIRECTION + " TEXT, " +
                COLUMN_START_LOCATION + " TEXT, " +
                COLUMN_END_LOCATION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SCHEDULE_BUS_NUMBER + ") REFERENCES " + TABLE_BUS + "(" + COLUMN_BUS_NUMBER + "))";
        db.execSQL(createBusScheduleTable);

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOKING_SCHEDULE_ID + " INTEGER, " +
                COLUMN_BOOKING_BUS_NUMBER + " VARCHAR(20), " +
                COLUMN_SEAT_NUMBER + " INTEGER, " +
                COLUMN_BOOKING_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULE + "(" + COLUMN_SCHEDULE_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BOOKING_BUS_NUMBER + ") REFERENCES " + TABLE_BUS + "(" + COLUMN_BUS_NUMBER + "), " +
                "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createBookingsTable);

        String createRatingsTable = "CREATE TABLE " + TABLE_RATINGS + " (" +
                COLUMN_RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RATING_USER_EMAIL + " TEXT, " +
                COLUMN_RATING_BUS_NUMBER + " VARCHAR(20), " +
                COLUMN_RATING_VALUE + " REAL)";
        db.execSQL(createRatingsTable);

        String createCancelBookingsTable = "CREATE TABLE " + TABLE_CANCEL_BOOKINGS + " (" +
                COLUMN_CANCEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CANCEL_BOOKING_ID + " INTEGER, " +
                COLUMN_CANCEL_BUS_NUMBER + " VARCHAR(20), " +
                COLUMN_CANCEL_SEAT_NUMBER + " INTEGER, " +
                COLUMN_CANCEL_STATE + " TEXT, " +
                COLUMN_CANCEL_USER_EMAIL + " TEXT)";
        db.execSQL(createCancelBookingsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_TYPE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_BUS + " ADD COLUMN " + COLUMN_DEPARTURE_INTERVAL + " INTEGER");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BUS + " (" +
                    COLUMN_BUS_NUMBER + " VARCHAR(20) PRIMARY KEY, " +
                    COLUMN_BUS_OWNER_ID + " INTEGER, " +
                    COLUMN_BUS_SEATS + " INTEGER, " +
                    COLUMN_DEPARTURE_INTERVAL + " INTEGER)");
        }
        if (oldVersion < 5) {
            String createBusScheduleTable = "CREATE TABLE " + TABLE_BUS_SCHEDULE + " (" +
                    COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SCHEDULE_BUS_NUMBER + " VARCHAR(20), " +
                    COLUMN_DAY + " TEXT, " +
                    COLUMN_TRIP_TIME + " TEXT, " +
                    COLUMN_TRIP_DIRECTION + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_SCHEDULE_BUS_NUMBER + ") REFERENCES " + TABLE_BUS + "(" + COLUMN_BUS_NUMBER + "))";
            db.execSQL(createBusScheduleTable);
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_BUS_SCHEDULE + " ADD COLUMN " + COLUMN_START_LOCATION + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BUS_SCHEDULE + " ADD COLUMN " + COLUMN_END_LOCATION + " TEXT");
        }
        if (oldVersion < 7) {
            String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                    COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BOOKING_SCHEDULE_ID + " INTEGER, " +
                    COLUMN_BOOKING_BUS_NUMBER + " VARCHAR(20), " +
                    COLUMN_SEAT_NUMBER + " INTEGER, " +
                    COLUMN_BOOKING_USER_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULE + "(" + COLUMN_SCHEDULE_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_BOOKING_BUS_NUMBER + ") REFERENCES " + TABLE_BUS + "(" + COLUMN_BUS_NUMBER + "), " +
                    "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
            db.execSQL(createBookingsTable);
        }
        if (oldVersion < 8) {
            String createRatingsTable = "CREATE TABLE " + TABLE_RATINGS + " (" +
                    COLUMN_RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RATING_USER_EMAIL + " TEXT, " +
                    COLUMN_RATING_BUS_NUMBER + " VARCHAR(20), " +
                    COLUMN_RATING_VALUE + " REAL)";
            db.execSQL(createRatingsTable);
        }
        if (oldVersion < 9) {
            if (!isColumnExists(db, TABLE_BUS, COLUMN_DRIVER)) {
                db.execSQL("ALTER TABLE " + TABLE_BUS + " ADD COLUMN " + COLUMN_DRIVER + " TEXT");
            }
        }
        if (oldVersion < 10) {
            String createCancelBookingsTable = "CREATE TABLE " + TABLE_CANCEL_BOOKINGS + " (" +
                    COLUMN_CANCEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CANCEL_BOOKING_ID + " INTEGER, " +
                    COLUMN_CANCEL_BUS_NUMBER + " VARCHAR(20), " +
                    COLUMN_CANCEL_SEAT_NUMBER + " INTEGER, " +
                    COLUMN_CANCEL_STATE + " TEXT, " +
                    COLUMN_CANCEL_USER_EMAIL + " TEXT)";
            db.execSQL(createCancelBookingsTable);
        }
    }

    private boolean isColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = null;
        try {
            // Query the database to check for the column
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String existingColumn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    if (columnName.equals(existingColumn)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if column exists: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
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
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + ", " + COLUMN_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_TYPE + " = ?", new String[]{"Bus driver"});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                drivers.add(name + " (" + email + ")");
            }
            cursor.close();
        }
        db.close();

        return drivers;
    }

    public boolean insertBus(String busNumber, int busSeats, String driver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUS_NUMBER, busNumber);
        values.put(COLUMN_BUS_SEATS, busSeats);
        values.put(COLUMN_DRIVER, driver); // Store driver in the new column

        long result = db.insert(TABLE_BUS, null, values);
        db.close();
        return result != -1;
    }

    public boolean insertBusSchedule(String busNumber, String day, String tripTime, String startLocation, String endLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_BUS_NUMBER, busNumber);
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TRIP_TIME, tripTime);
        values.put(COLUMN_START_LOCATION, startLocation);
        values.put(COLUMN_END_LOCATION, endLocation);

        long result = db.insert(TABLE_BUS_SCHEDULE, null, values);
        db.close();
        return result != -1;
    }

    public boolean insertBooking(int scheduleID, String busNumber, int seatNumber, String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKING_SCHEDULE_ID, scheduleID);
        values.put(COLUMN_BOOKING_BUS_NUMBER, busNumber);
        values.put(COLUMN_SEAT_NUMBER, seatNumber);
        values.put(COLUMN_BOOKING_USER_ID, userID);

        long result = db.insert(TABLE_BOOKINGS, null, values);
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

    public List<Integer> getBookedSeats(int scheduleID) {
        List<Integer> bookedSeats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS, new String[]{COLUMN_SEAT_NUMBER}, COLUMN_BOOKING_SCHEDULE_ID + "=?", new String[]{String.valueOf(scheduleID)}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                bookedSeats.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER)));
            }
            cursor.close();
        }
        db.close();
        return bookedSeats;
    }

    public List<String> getBusesForRouteAndDate(String startLocation, String endLocation, String tripDate) {
        List<String> buses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_BUS_NUMBER + " FROM " + TABLE_BUS_SCHEDULE + " WHERE " +
                        COLUMN_START_LOCATION + " = ? AND " + COLUMN_END_LOCATION + " = ? AND " + COLUMN_DAY + " = ?",
                new String[]{startLocation, endLocation, tripDate});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                buses.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUS_NUMBER)));
            }
            cursor.close();
        }
        db.close();
        return buses;
    }

    public int getTotalSeats(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUS, new String[]{COLUMN_BUS_SEATS}, COLUMN_BUS_NUMBER + "=?", new String[]{busNumber}, null, null, null);
        int totalSeats = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalSeats = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUS_SEATS));
            cursor.close();
        }
        db.close();
        return totalSeats;
    }

    public boolean isSeatAlreadyBooked(String userID, int scheduleID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS, new String[]{COLUMN_BOOKING_ID},
                COLUMN_BOOKING_USER_ID + "=? AND " + COLUMN_BOOKING_SCHEDULE_ID + "=?",
                new String[]{String.valueOf(userID), String.valueOf(scheduleID)}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getScheduleID(String busNumber, String tripDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUS_SCHEDULE, new String[]{COLUMN_SCHEDULE_ID},
                COLUMN_SCHEDULE_BUS_NUMBER + "=? AND " + COLUMN_DAY + "=?",
                new String[]{busNumber, tripDate}, null, null, null);
        int scheduleID = -1;
        if (cursor != null && cursor.moveToFirst()) {
            scheduleID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID));
            cursor.close();
        }
        db.close();
        return scheduleID;
    }

    public List<String> getBusesForRouteAndDate(String busNumber) {
        List<String> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DAY + ", " + COLUMN_TRIP_TIME + ", " + COLUMN_START_LOCATION + ", " + COLUMN_END_LOCATION + " FROM " + TABLE_BUS_SCHEDULE + " WHERE " + COLUMN_SCHEDULE_BUS_NUMBER + " = ?", new String[]{busNumber});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                String tripTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_TIME));
                String startLocation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_LOCATION));
                String endLocation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_LOCATION));
                trips.add(day + " " + tripTime + " " + startLocation + " to " + endLocation);
            }
            cursor.close();
        }
        db.close();
        return trips;
    }
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { COLUMN_ID }, COLUMN_EMAIL + "=?", new String[] { email },
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
            return userId;
        }
        return -1; // Return -1 if user not found
    }

    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b." + COLUMN_BOOKING_ID + ", b." + COLUMN_BOOKING_SCHEDULE_ID + ", b." + COLUMN_BOOKING_BUS_NUMBER +
                ", b." + COLUMN_SEAT_NUMBER + ", s." + COLUMN_START_LOCATION + " || ' to ' || s." + COLUMN_END_LOCATION + " AS route " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_BUS_SCHEDULE + " s ON b." + COLUMN_BOOKING_SCHEDULE_ID + " = s." + COLUMN_SCHEDULE_ID +
                " WHERE b." + COLUMN_BOOKING_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[] { userId });
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID));
                int scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_SCHEDULE_ID));
                String busNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_BUS_NUMBER));
                int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER));
                String route = cursor.getString(cursor.getColumnIndexOrThrow("route"));
                bookings.add(new Booking(bookingId, scheduleId, busNumber, seatNumber, userId, route));
            }
            cursor.close();
        }
        db.close();
        return bookings;
    }

    public void saveRating(String userEmail, String busNumber, float ratingValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (ratingExists(userEmail, busNumber)) {
            // Update existing rating
            ContentValues values = new ContentValues();
            values.put(COLUMN_RATING_VALUE, ratingValue);
            db.update(TABLE_RATINGS, values, COLUMN_RATING_USER_EMAIL + "=? AND " + COLUMN_RATING_BUS_NUMBER + "=?",
                    new String[] { userEmail, busNumber });
            Log.d("DatabaseHelper",
                    "Rating updated: " + ratingValue + " for bus: " + busNumber + " by user: " + userEmail);
        } else {
            // Insert new rating
            ContentValues values = new ContentValues();
            values.put(COLUMN_RATING_USER_EMAIL, userEmail);
            values.put(COLUMN_RATING_BUS_NUMBER, busNumber);
            values.put(COLUMN_RATING_VALUE, ratingValue);
            db.insert(TABLE_RATINGS, null, values);
            Log.d("DatabaseHelper",
                    "Rating saved: " + ratingValue + " for bus: " + busNumber + " by user: " + userEmail);
        }
        db.close();
    }

    public float getRating(String userEmail, String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATINGS, new String[] { COLUMN_RATING_VALUE },
                COLUMN_RATING_USER_EMAIL + "=? AND " + COLUMN_RATING_BUS_NUMBER + "=?",
                new String[] { userEmail, busNumber }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            float ratingValue = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING_VALUE));
            cursor.close();
            return ratingValue;
        }
        return 0; // Default rating value if no rating found
    }

    private boolean ratingExists(String userEmail, String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATINGS, new String[] { COLUMN_RATING_ID },
                COLUMN_RATING_USER_EMAIL + "=? AND " + COLUMN_RATING_BUS_NUMBER + "=?",
                new String[] { userEmail, busNumber }, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public float getAverageRating(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(" + COLUMN_RATING_VALUE + ") FROM " + TABLE_RATINGS + " WHERE "
                + COLUMN_RATING_BUS_NUMBER + "=?", new String[] { busNumber });
        if (cursor != null && cursor.moveToFirst()) {
            float averageRating = cursor.getFloat(0);
            cursor.close();
            return averageRating;
        }
        return 0; // Default rating value if no ratings found
    }
    public List<Booking> getUserBookings(String userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_BOOKING_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID));
                int scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_SCHEDULE_ID));
                String busNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_BUS_NUMBER));
                int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER));
                bookings.add(new Booking(bookingId, scheduleId, busNumber, seatNumber, userId,null));
            }
            cursor.close();
        }
        db.close();
        return bookings;
    }
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
        if (cursor != null && cursor.moveToFirst()) {
            int scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_SCHEDULE_ID));
            String busNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_BUS_NUMBER));
            int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_USER_ID));
            cursor.close();
            return new Booking(bookingId, scheduleId, busNumber, seatNumber, userId,null);
        }
        return null;
    }
    public List<Integer> getBookedSeatsForSchedule(int scheduleId) {
        List<Integer> bookedSeats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SEAT_NUMBER + " FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_BOOKING_SCHEDULE_ID + "=?", new String[]{String.valueOf(scheduleId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER));
                bookedSeats.add(seatNumber);
            }
            cursor.close();
        }
        db.close();
        return bookedSeats;
    }
    public int getBusTotalSeats(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_BUS_SEATS + " FROM " + TABLE_BUS + " WHERE " + COLUMN_BUS_NUMBER + "=?", new String[]{busNumber});
        if (cursor != null && cursor.moveToFirst()) {
            int totalSeats = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUS_SEATS));
            cursor.close();
            return totalSeats;
        }
        return 0; // Default value if no seats found
    }
    public void updateSeatNumber(int bookingId, int newSeatNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEAT_NUMBER, newSeatNumber);
        db.update(TABLE_BOOKINGS, values, COLUMN_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
        db.close();
    }
    public List<Booking> getBookingsForUser(String userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKINGS + " b " +
                "WHERE b." + COLUMN_BOOKING_USER_ID + " = ? " +
                "AND b." + COLUMN_BOOKING_ID + " NOT IN (SELECT " + COLUMN_CANCEL_BOOKING_ID + " FROM " + TABLE_CANCEL_BOOKINGS + ")";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID));
                int scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_SCHEDULE_ID));
                String busNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_BUS_NUMBER));
                int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEAT_NUMBER));
                bookings.add(new Booking(bookingId, scheduleId, busNumber, seatNumber, userId,null));
            }
            cursor.close();
        }
        db.close();
        return bookings;
    }
    public Cursor getAvailableBuses(String startLocation, String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Get the current time and add 2 hours
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = new Date();
            String currentTime = timeFormat.format(now);

            // Calculate the time 2 hours later
            Date twoHoursLater = new Date(now.getTime() + (2 * 60 * 60 * 1000));
            String timePlusTwoHours = timeFormat.format(twoHoursLater);

            // SQL query with time filtering
            String query = "SELECT " + COLUMN_SCHEDULE_BUS_NUMBER + ", " +
                    COLUMN_TRIP_TIME + ", " +
                    COLUMN_END_LOCATION +
                    " FROM " + TABLE_BUS_SCHEDULE +
                    " WHERE " + COLUMN_START_LOCATION + " = ? AND " +
                    COLUMN_DAY + " = ? AND " +
                    "strftime('%H:%M', " + COLUMN_TRIP_TIME + ") BETWEEN ? AND ?";

            // Execute query with parameters
            cursor = db.rawQuery(query, new String[]{startLocation, day, currentTime, timePlusTwoHours});
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching available buses: " + e.getMessage());
        }
        return cursor;
    }
    public boolean insertCancelBooking(int bookingId, String busNumber, int seatNumber, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CANCEL_BOOKING_ID, bookingId);
        values.put(COLUMN_CANCEL_BUS_NUMBER, busNumber);
        values.put(COLUMN_CANCEL_SEAT_NUMBER, seatNumber);
        values.put(COLUMN_CANCEL_STATE, "pending");
        values.put(COLUMN_CANCEL_USER_EMAIL, userEmail);

        long result = db.insert(TABLE_CANCEL_BOOKINGS, null, values);
        db.close();
        Log.d("DatabaseHelper",
                "CancelBooking Saved: " + bookingId + " for bus: " + busNumber + " by user: " + userEmail + " with state: pending");
        return result != -1;
    }
    public List<CancelRequest> getCancelRequestsForDriver(String driverEmail) {
        List<CancelRequest> cancelRequests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CANCEL_BOOKINGS + " cb " +
                "JOIN " + TABLE_BUS + " b ON cb." + COLUMN_CANCEL_BUS_NUMBER + " = b." + COLUMN_BUS_NUMBER + " " +
                "WHERE b." + COLUMN_DRIVER + " = ? AND cb." + COLUMN_CANCEL_STATE + " = 'pending'";
        Cursor cursor = db.rawQuery(query, new String[]{driverEmail});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cancelId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_ID));
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_BOOKING_ID));
                String busNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_BUS_NUMBER));
                int seatNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_SEAT_NUMBER));
                String state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_STATE));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CANCEL_USER_EMAIL));
                cancelRequests.add(new CancelRequest(cancelId, bookingId, busNumber, seatNumber, state, userEmail));
            }
            cursor.close();
        }
        db.close();
        return cancelRequests;
    }
    public boolean acceptCancelRequest(int cancelId, int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Delete the booking
            int rowsDeleted = db.delete(TABLE_BOOKINGS, COLUMN_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
            if (rowsDeleted == 0) {
                db.endTransaction();
                return false;
            }

            // Update the cancel request state
            ContentValues values = new ContentValues();
            values.put(COLUMN_CANCEL_STATE, "completed");
            int rowsUpdated = db.update(TABLE_CANCEL_BOOKINGS, values, COLUMN_CANCEL_ID + "=?", new String[]{String.valueOf(cancelId)});
            if (rowsUpdated == 0) {
                db.endTransaction();
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean rejectCancelRequest(int cancelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CANCEL_BOOKINGS, COLUMN_CANCEL_ID + "=?", new String[]{String.valueOf(cancelId)});
        db.close();
        return rowsDeleted > 0;
    }
}