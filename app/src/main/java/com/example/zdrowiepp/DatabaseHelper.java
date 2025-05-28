package com.example.zdrowiepp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, 3);
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
                        "FOREIGN KEY(" + Constant.COL_USER_ID + ") REFERENCES " + Constant.TAB_USERS + "(" + Constant.COL_ID + ")" +
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
                        "FOREIGN KEY(" + Constant.COL_TRAINING_PLAN_ID + ") REFERENCES " + Constant.TAB_TRAINING_PLAN + "(" + Constant.COL_ID + ")" +
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
                        "FOREIGN KEY(" + Constant.COL_EXERCISE_ID + ") REFERENCES " + Constant.TAB_EXERCISES + "(" + Constant.COL_ID + ")" +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + Constant.TAB_STEPS + " (" +
                        Constant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Constant.COL_USER_ID + " INTEGER, " +
                        Constant.COL_COUNT + " INTEGER, " +
                        Constant.COL_DATE + " DATETIME, " +
                        "FOREIGN KEY(" + Constant.COL_USER_ID + ") REFERENCES " + Constant.TAB_USERS + "(" + Constant.COL_ID + ")" +
                        ");"
        );

        // Dodatkowa tabela TrainingPlans (z Twojego kodu)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS TrainingPlans (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "userId INTEGER NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_STEPS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_EXERCISES_HISTORY + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_EXERCISES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_TRAINING_PLAN + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TAB_USERS + ";");
        db.execSQL("DROP TABLE IF EXISTS TrainingPlans;");
        onCreate(db);
    }

    // Metoda do pobierania ćwiczeń po Id planu treningowego
    public List<Exercise> selectExercisesByTrainingPlanId(int trainingPlanId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.TAB_EXERCISES, null, Constant.COL_TRAINING_PLAN_ID + " = ?", new String[]{String.valueOf(trainingPlanId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Constant.COL_NAME));
                byte minutes = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_MINUTES));
                byte seconds = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SECONDS));
                short sets = (short) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SETS));
                short reps = (short) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_REPS));

                exercises.add(new Exercise(id, trainingPlanId, name, minutes, seconds, sets, reps));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return exercises;
    }

    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.COL_EMAIL, email);
        contentValues.put(Constant.COL_PASSWORD, password);

        long result = db.insert(Constant.TAB_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + Constant.TAB_USERS +
                        " WHERE " + Constant.COL_EMAIL + "=? AND " + Constant.COL_PASSWORD + "=?", new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getPassword(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + Constant.COL_PASSWORD +
                        " FROM " + Constant.TAB_USERS +
                        " WHERE " + Constant.COL_EMAIL + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String password = cursor.getString(0);
            cursor.close();
            db.close();
            return password;
        }

        cursor.close();
        db.close();
        return null;
    }

    public void insertExercise(Exercise exercise) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COL_TRAINING_PLAN_ID, exercise.getTrainingPlanId());
        values.put(Constant.COL_NAME, exercise.getName());
        values.put(Constant.COL_SETS, exercise.getSets());
        values.put(Constant.COL_REPS, exercise.getReps());
        values.put(Constant.COL_MINUTES, exercise.getMinutes());
        values.put(Constant.COL_SECONDS, exercise.getSeconds());

        db.insert(Constant.TAB_EXERCISES, null, values);
        db.close();
    }

    public TrainingPlan selectTrainingPlan(int id) {
        SQLiteDatabase db = getReadableDatabase();
        TrainingPlan plan = null;

        Cursor cursor = db.query("TrainingPlans",
                new String[]{"id", "name", "userId"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int planId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));

                plan = new TrainingPlan(planId, name, userId);
            }
            cursor.close();
        }
        db.close();
        return plan;
    }

    public int insertTrainingPlan(TrainingPlan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", plan.getName());
        values.put("userId", plan.getUserId());

        long insertedId = db.insert("TrainingPlans", null, values);

        return (int) insertedId;
    }
    public Exercise selectExercise(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + Constant.TAB_EXERCISES + " WHERE " + Constant.COL_ID + " = ?", new String[]{Integer.toString(id)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }

        int trainingPlanId = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_TRAINING_PLAN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Constant.COL_NAME));
        byte minutes = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_MINUTES));
        byte seconds = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SECONDS));
        short sets = cursor.getShort(cursor.getColumnIndexOrThrow(Constant.COL_SETS));
        short reps = cursor.getShort(cursor.getColumnIndexOrThrow(Constant.COL_REPS));

        cursor.close();
        db.close();
        return new Exercise(id, trainingPlanId, name, minutes, seconds, sets, reps);
    }

    public int getUserId(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Constant.COL_ID + " FROM " + Constant.TAB_USERS + " WHERE " + Constant.COL_EMAIL + "=?", new String[]{email});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public List<TrainingPlan> selectAllTrainingPlans() {
        List<TrainingPlan> plans = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, userId FROM TrainingPlans", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int userId = cursor.getInt(2);

                plans.add(new TrainingPlan(id, name, userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    public void deleteExercise(int exerciseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete("exercises", "id = ?", new String[]{String.valueOf(exerciseId)});
        } finally {
            db.close();
        }
    }

    public void insertExerciseHistory(int exerciseId, Date date, int sets, int hours, int minutes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COL_EXERCISE_ID, exerciseId);
        values.put(Constant.COL_DATE, date.getTime());
        values.put(Constant.COL_SETS, sets);
        values.put(Constant.COL_HOURS, hours);
        values.put(Constant.COL_MINUTES, minutes);
        db.insert(Constant.TAB_EXERCISES_HISTORY, null, values);
        db.close();
    }

    public int getLastInsertedTrainingPlanId(int userId) {
        int lastId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT MAX(id) FROM TrainingPlans WHERE userId = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0);
            }
        }
        return lastId;
    }

    public void deleteTrainingPlan(int planId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("TrainingPlans", "id = ?", new String[]{String.valueOf(planId)});
        db.delete("exercises", "trainingPlanId = ?", new String[]{String.valueOf(planId)}); // usuń też ćwiczenia powiązane
        db.close();
    }

    public List<ExerciseHistoryItem> getExerciseHistoryBetween(Date startDate, Date endDate) {
        List<ExerciseHistoryItem> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT eh." + Constant.COL_SETS + ", eh." + Constant.COL_HOURS + ", eh." + Constant.COL_MINUTES + ", " +
                        "eh." + Constant.COL_DATE + ", ex." + Constant.COL_NAME + " AS exercise_name " +
                        "FROM " + Constant.TAB_EXERCISES_HISTORY + " eh " +
                        "JOIN " + Constant.TAB_EXERCISES + " ex ON eh." + Constant.COL_EXERCISE_ID + " = ex." + Constant.COL_ID + " " +
                        "WHERE eh." + Constant.COL_DATE + " BETWEEN ? AND ? " +
                        "ORDER BY eh." + Constant.COL_DATE + " DESC";

        String[] args = {
                String.valueOf(startDate.getTime()),
                String.valueOf(endDate.getTime())
        };

        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                ExerciseHistoryItem item = new ExerciseHistoryItem();
                item.setExerciseName(cursor.getString(cursor.getColumnIndexOrThrow("exercise_name")));
                item.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Constant.COL_DATE))));
                item.setSets(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SETS)));
                item.setHours(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_HOURS)));
                item.setMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_MINUTES)));
                result.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }





}
