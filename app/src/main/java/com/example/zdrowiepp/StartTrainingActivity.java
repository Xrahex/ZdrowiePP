package com.example.zdrowiepp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import java.util.List;

public class StartTrainingActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private Button endTrainingButton;
    private ListView exerciseListView;
    private ExerciseAdapter exerciseAdapter;
    private TrainingPlan trainingPlan;
    private int planId;
    private List<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_training);

        planId = getIntent().getIntExtra("PLAN_ID", 0);
        trainingPlan = new TrainingPlan(this, planId);

        chronometer = findViewById(R.id.chronometer);
        endTrainingButton = findViewById(R.id.endTrainingButton);
        exerciseListView = findViewById(R.id.exerciseListView);

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            exercises = db.selectExercisesByTrainingPlanId(planId);
        }

        // Adapter z możliwością oznaczania wykonania
        exerciseAdapter = new ExerciseAdapter(this, exercises, true); // true = tryb wykonywania
        exerciseListView.setAdapter(exerciseAdapter);

        endTrainingButton.setOnClickListener(v -> {
            chronometer.stop();
            saveExerciseHistory();
            Toast.makeText(this, "Ćwiczenia zapisane", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveExerciseHistory() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int minutes = (int) (elapsedMillis / 1000 / 60);
        int hours = minutes / 60;
        minutes = minutes % 60;

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            for (Exercise ex : exerciseAdapter.getCheckedExercises()) {
                db.insertExerciseHistory(ex.getId(), new Date(), ex.getSets(), hours, minutes);
            }
        }
    }
}
