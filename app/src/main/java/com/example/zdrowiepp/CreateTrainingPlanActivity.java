package com.example.zdrowiepp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CreateTrainingPlanActivity extends AppCompatActivity {
    private EditText nameEditText;
    private Button saveButton, addExerciseButton, startTraningButton;
    private ListView exercisesListView;
    private ExerciseAdapter exerciseAdapter;
    private TrainingPlan trainingPlan;
    private int planId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training_plan);

        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        addExerciseButton = findViewById(R.id.addExerciseButton);
        startTraningButton = findViewById(R.id.startTraning);
        exercisesListView = findViewById(R.id.exercisesListView);

        planId = getIntent().getIntExtra("PLAN_ID", 0);

        if (planId != 0) {
            trainingPlan = new TrainingPlan(this, planId);
            nameEditText.setText(trainingPlan.getName());
        } else {
            trainingPlan = new TrainingPlan(0, "", 0); // nowy pusty plan
        }



        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (!name.isEmpty()) {
                trainingPlan.setName(name);
                trainingPlan.setUserId(1); // <- Ustaw prawidłowy userId tutaj
                trainingPlan.saveTrainingPlan(this);

                // Po zapisie pobierz aktualne ID planu (zakładam, że metoda zwraca lub ustawia ID)
                if (trainingPlan.getId() == 0) {
                    int newId = trainingPlan.fetchLatestId(this);
                    trainingPlan.setId(newId);
                }

                Toast.makeText(this, "Plan zapisany", Toast.LENGTH_SHORT).show();
                refreshExercises();
            } else {
                Toast.makeText(this, "Podaj nazwę planu", Toast.LENGTH_SHORT).show();
            }
        });

        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateTrainingPlanActivity.this, CreateExerciseActivity.class);
            intent.putExtra("trainingPlanId", trainingPlan.getId());
            startActivity(intent);
        });

        refreshExercises();

        startTraningButton.setOnClickListener(v -> {
            if (trainingPlan.getId() == 0) {
                Toast.makeText(this, "Najpierw zapisz plan", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CreateTrainingPlanActivity.this, StartTrainingActivity.class);
            intent.putExtra("PLAN_ID", trainingPlan.getId());
            startActivity(intent);
        });

    }

    private void refreshExercises() {
        if (trainingPlan.getId() != 0) {
            try (DatabaseHelper db = new DatabaseHelper(this)) {
                var exercises = db.selectExercisesByTrainingPlanId(trainingPlan.getId());
                if (exercises == null || exercises.isEmpty()) {
                    Toast.makeText(this, "Brak ćwiczeń w planie", Toast.LENGTH_SHORT).show();
                }
                exerciseAdapter = new ExerciseAdapter(this, exercises);
                exercisesListView.setAdapter(exerciseAdapter);
            }
        } else {
            Toast.makeText(this, "Plan nie ma ID, lista ćwiczeń nie może zostać załadowana", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshExercises();
    }
}
