package com.example.zdrowiepp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class CreateTrainingPlanActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int trainingPlanId = -1;

    private EditText etExerciseName, etSets, etReps, etMinutes, etSeconds;
    private Button btnAddExercise, btnSavePlan;

    private final List<Exercise> exercisesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training_plan);

        dbHelper = new DatabaseHelper(this);

        etExerciseName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etMinutes = findViewById(R.id.etMinutes);
        etSeconds = findViewById(R.id.etSeconds);

        btnAddExercise = findViewById(R.id.btnAddExercise);
        btnSavePlan = findViewById(R.id.btnSavePlan);

        btnAddExercise.setOnClickListener(v -> addExercise());
        btnSavePlan.setOnClickListener(v -> saveTrainingPlan());
    }

    private void addExercise() {
        String name = etExerciseName.getText().toString().trim();
        int sets = parseIntOrZero(etSets.getText().toString());
        int reps = parseIntOrZero(etReps.getText().toString());
        int minutes = parseIntOrZero(etMinutes.getText().toString());
        int seconds = parseIntOrZero(etSeconds.getText().toString());

        if (name.isEmpty()) {
            Toast.makeText(this, "Podaj nazwę ćwiczenia", Toast.LENGTH_SHORT).show();
            return;
        }

        Exercise exercise = new Exercise(0, 0, name, (byte)minutes, (byte)seconds, (short)sets, (short)reps);
        exercisesList.add(exercise);

        Toast.makeText(this, "Dodano ćwiczenie: " + name, Toast.LENGTH_SHORT).show();

        etExerciseName.setText("");
        etSets.setText("");
        etReps.setText("");
        etMinutes.setText("");
        etSeconds.setText("");
    }

    private void saveTrainingPlan() {
        if (exercisesList.isEmpty()) {
            Toast.makeText(this, "Dodaj przynajmniej jedno ćwiczenie", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = MyApp.getUserId(getApplicationContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.COL_USER_ID, userId);

        long planId = db.insert(Constant.TAB_TRAINING_PLAN, null, values);
        if (planId == -1) {
            Toast.makeText(this, "Błąd zapisu planu", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Exercise e : exercisesList) {
            ContentValues cv = new ContentValues();
            cv.put(Constant.COL_TRAINING_PLAN_ID, planId);
            cv.put(Constant.COL_NAME, e.getName());
            cv.put(Constant.COL_SETS, e.getSets());
            cv.put(Constant.COL_REPS, e.getReps());
            cv.put(Constant.COL_MINUTES, e.getMinutes());
            cv.put(Constant.COL_SECONDS, e.getSeconds());

            db.insert(Constant.TAB_EXERCISES, null, cv);
        }

        Toast.makeText(this, "Plan treningowy zapisany", Toast.LENGTH_LONG).show();
        finish();
    }


    private int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
