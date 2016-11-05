package com.example.marc.codingproject.childrensblockpuzzle.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "statistics";
    private static String TABLE_NAME = "app_db";
    public static String KEY_ID = "_id";
    public static String KEY_TOUCHES = "touches";
    private List<Model> all;

    /**
     * Initialize the database
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ").append(TABLE_NAME)
                .append("(").append(KEY_ID).append(" INTEGER PRIMARY KEY")
                .append(",").append(KEY_TOUCHES).append(" INTEGER)");

        db.execSQL(query.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllCursor() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean increaseTouch(Bitmap bitmap, int touches) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TOUCHES, touches);
            getReadableDatabase().insert(TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public List<Model> getAll() {
        return all;
    }
}
