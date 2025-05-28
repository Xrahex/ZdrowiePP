package com.example.zdrowiepp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CreateExerciseActivity extends AppCompatActivity {
    private EditText nameEditText, minutesEditText, secondsEditText, setsEditText, repsEditText;
    private Button saveExerciseButton;
    private int trainingPlanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);

        nameEditText = findViewById(R.id.nameEditText);
        minutesEditText = findViewById(R.id.minutesEditText);
        secondsEditText = findViewById(R.id.secondsEditText);
        setsEditText = findViewById(R.id.setsEditText);
        repsEditText = findViewById(R.id.repsEditText);
        saveExerciseButton = findViewById(R.id.saveExerciseButton);

        trainingPlanId = getIntent().getIntExtra("trainingPlanId", 0);

        saveExerciseButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String minutesStr = minutesEditText.getText().toString().trim();
            String secondsStr = secondsEditText.getText().toString().trim();
            String setsStr = setsEditText.getText().toString().trim();
            String repsStr = repsEditText.getText().toString().trim();

            if (name.isEmpty() || minutesStr.isEmpty() || secondsStr.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            byte minutes, seconds;
            short sets, reps;
            try {
                minutes = Byte.parseByte(minutesStr);
                seconds = Byte.parseByte(secondsStr);
                sets = Short.parseShort(setsStr);
                reps = Short.parseShort(repsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Podaj poprawne liczby", Toast.LENGTH_SHORT).show();
                return;
            }

            Exercise exercise = new Exercise(0, trainingPlanId, name, minutes, seconds, sets, reps);
            exercise.saveExercise(this, trainingPlanId, name, minutes, seconds, sets, reps);
            finish();
        });
    }
}
