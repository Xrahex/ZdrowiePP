package com.example.zdrowiepp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlanDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ArrayList<String> exerciseList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private int planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        listView = findViewById(R.id.listViewExercises);
        dbHelper = new DatabaseHelper(this);

        planId = getIntent().getIntExtra("PLAN_ID", -1);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exerciseList);
        listView.setAdapter(adapter);

        loadExercises();
    }

    private void loadExercises() {
        exerciseList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                Constant.TAB_EXERCISES,
                null,
                Constant.COL_TRAINING_PLAN_ID + "=?",
                new String[]{String.valueOf(planId)},
                null, null, null
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Constant.COL_NAME));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SETS));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_REPS));
            int min = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_MINUTES));
            int sec = cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COL_SECONDS));
            exerciseList.add(name + " - " + sets + "x" + reps + " (" + min + "m " + sec + "s)");
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
