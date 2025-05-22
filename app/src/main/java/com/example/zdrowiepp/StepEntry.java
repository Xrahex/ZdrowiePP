package com.example.zdrowiepp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StepEntry {
    private int id;
    private int userId;
    private int count;
    private String date;

    public StepEntry(int id, int userId, int count, String date) {
        this.id = id;
        this.userId = userId;
        this.count = count;
        this.date = date;
    }

    public StepEntry(int userId, int count, String date) {
        this(-1, userId, count, date);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean save(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constant.COL_ID + " FROM " + Constant.TAB_STEPS +
                        " WHERE " + Constant.COL_USER_ID + "=? AND " + Constant.COL_DATE + "=?",
                new String[] { String.valueOf(userId), date }
        );

        ContentValues values = new ContentValues();
        values.put(Constant.COL_USER_ID, userId);
        values.put(Constant.COL_COUNT, count);
        values.put(Constant.COL_DATE, date);

        boolean success;
        if (cursor.moveToFirst()) {
            int existingId = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_ID));
            success = db.update(Constant.TAB_STEPS, values, Constant.COL_ID + "=?", new String[]{String.valueOf(existingId)}) > 0;
            this.id = existingId;
        } else {
            long newId = db.insert(Constant.TAB_STEPS, null, values);
            success = newId != -1;
            if (success) this.id = (int) newId;
        }

        cursor.close();
        return success;
    }

    public static List<StepEntry> getLast7Days(DatabaseHelper dbHelper, int userId) {
        List<StepEntry> steps = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + Constant.TAB_STEPS +
                        " WHERE " + Constant.COL_USER_ID + "=? " +
                        " ORDER BY " + Constant.COL_DATE + " DESC LIMIT 7",
                new String[]{ String.valueOf(userId) }
        );

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_ID));
            int count = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_COUNT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(Constant.COL_DATE));
            steps.add(new StepEntry(id, userId, count, date));
        }

        cursor.close();
        return steps;
    }

    public static StepEntry getForDate(DatabaseHelper dbHelper, int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StepEntry stepEntry = null;

        String selection = "userId = ? AND date = ?";
        String[] selectionArgs = new String[] { String.valueOf(userId), date };

        Cursor cursor = db.query("steps", null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
                String dateFromDb = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                stepEntry = new StepEntry(id, userId, count, dateFromDb);
            }
            cursor.close();
        }
        return stepEntry;
    }

}
