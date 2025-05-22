package com.example.zdrowiepp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Constant.TAB_USERS + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_EMAIL + " TEXT UNIQUE, " +
                        Constant.COL_PASSWORD + " TEXT" +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + Constant.TAB_TRAINING_PLAN + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_USER_ID + " INTEGER, " +
                        "FOREIGN KEY(" + Constant.COL_USER_ID + ") REFERENCES " + Constant.TAB_USERS +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + Constant.TAB_EXERCISES + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_TRAINING_PLAN_ID + " INTEGER, " +
                        Constant.COL_NAME + " TEXT, " +
                        Constant.COL_SETS + " INTEGER, " +
                        Constant.COL_REPS + " INTEGER, " +
                        Constant.COL_MINUTES + " INTEGER, " +
                        Constant.COL_SECONDS + " INTEGER, " +
                        "FOREIGN KEY(" + Constant.COL_TRAINING_PLAN_ID + ") REFERENCES " + Constant.TAB_TRAINING_PLAN +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + Constant.TAB_EXERCISES_HISTORY + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_EXERCISE_ID + " INTEGER, " +
                        Constant.COL_DATE + " DATETIME, " +
                        Constant.COL_SETS + " INTEGER, " +
                        Constant.COL_HOURS + " INTEGER, " +
                        Constant.COL_MINUTES + " INTEGER, " +
                        "FOREIGN KEY(" + Constant.COL_EXERCISE_ID + ") REFERENCES " + Constant.TAB_EXERCISES +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + Constant.TAB_STEPS + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_USER_ID + " INTEGER, " +
                        Constant.COL_COUNT + " INTEGER, " +
                        Constant.COL_DATE + " DATETIME, " +
                        "FOREIGN KEY(" + Constant.COL_USER_ID + ") REFERENCES " +
                        Constant.TAB_USERS + "(" + Constant.COL_ID + ")" +
                        ");"
        );
    }
    /*
        db.execSQL(
            "CREATE TABLE " + Constant.TAB_STEPS + " (" +
                Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constant.COL_USER_ID + " INTEGER, " +
                Constant.COL_COUNT + " INTEGER, " +
                Constant.COL_DATE + " DATETIME, " +
                "FOREIGN KEY(" + Constant.COL_USER_ID + ") REFERENCES " + Constant.TAB_USERS +
            ");"
        );
    }
    */


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_STEPS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_EXERCISES_HISTORY + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_EXERCISES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_TRAINING_PLAN + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_USERS + ";");
        onCreate(db);
    }

    public boolean insertUser(String email, String password) {
        var db = getWritableDatabase();
        var contentValues = new ContentValues();
        contentValues.put(Constant.COL_EMAIL, email);
        contentValues.put(Constant.COL_PASSWORD, password);

        var result = db.insert(Constant.TAB_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        var cursor = getReadableDatabase().rawQuery(
                "SELECT *" +
                        " FROM " + Constant.TAB_USERS +
                        " WHERE " + Constant.COL_EMAIL + "=? AND " + Constant.COL_PASSWORD + "=?", new String[] { email, password });

        var exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getPassword(String email) {
        var db = getReadableDatabase();
        var cursor = db.rawQuery(
                "SELECT " + Constant.COL_PASSWORD +
                        " FROM " + Constant.TAB_USERS +
                        " WHERE " + Constant.COL_EMAIL + " = ?", new String[] { email });

        if (cursor.moveToFirst()) {
            var password = cursor.getString(0);
            cursor.close();
            return password;
        }

        cursor.close();
        return null;
    }

    public void insertExercise(Exercise exercise) {
        var values = new ContentValues();
        values.put(Constant.COL_TRAINING_PLAN_ID, exercise.getTrainingPlanId());
        values.put(Constant.COL_NAME, exercise.getName());
        values.put(Constant.COL_SETS, exercise.getSets());
        values.put(Constant.COL_REPS, exercise.getReps());
        values.put(Constant.COL_MINUTES, exercise.getMinutes());
        values.put(Constant.COL_SECONDS, exercise.getSeconds());
        getWritableDatabase().insert(Constant.TAB_EXERCISES, null, values);
    }

    public Exercise selectExercise(int id) {
        var cursor = getReadableDatabase().rawQuery(
                "SELECT *" +
                        " FROM " + Constant.TAB_EXERCISES +
                        " WHERE " + Constant.COL_EXERCISE_ID + " = ?", new String[]{ Integer.toString(id) }
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        var index = cursor.getColumnIndex(Constant.COL_TRAINING_PLAN_ID);
        if (index < 0) {
            cursor.close();
            return null;
        }

        var trainingPlanId = cursor.getInt(index);
        var name = "";
        index = cursor.getColumnIndex(Constant.COL_NAME);
        if (index >= 0) {
            name = cursor.getString(index);
        }

        byte minutes = 0;
        index = cursor.getColumnIndex(Constant.COL_MINUTES);
        if (index >= 0) {
            minutes = (byte)cursor.getShort(index);
        }

        byte seconds = 0;
        index = cursor.getColumnIndex(Constant.COL_SECONDS);
        if (index >= 0) {
            seconds = (byte)cursor.getShort(index);
        }

        short sets = 0;
        index = cursor.getColumnIndex(Constant.COL_SETS);
        if (index >= 0) {
            sets = cursor.getShort(index);
        }

        short reps = 0;
        index = cursor.getColumnIndex(Constant.COL_REPS);
        if (index >= 0) {
            reps = cursor.getShort(index);
        }

        cursor.close();
        return new Exercise(id, trainingPlanId, name, minutes, seconds, sets, reps);
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Constant.COL_ID + " FROM " + Constant.TAB_USERS + " WHERE " + Constant.COL_EMAIL + "=?", new String[]{email});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_ID));
        }
        cursor.close();
        return userId;
    }

}
