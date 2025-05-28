package com.example.zdrowiepp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private final List<Exercise> exercises;
    private final boolean selectionMode;
    private final List<Exercise> checkedExercises = new ArrayList<>();

    public ExerciseAdapter(Context context, List<Exercise> exercises) {
        this(context, exercises, false); // domyślnie bez trybu zaznaczania
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, boolean selectionMode) {
        super(context, 0, exercises);
        this.exercises = exercises;
        this.selectionMode = selectionMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_exercise, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.exercise_name_text);
        TextView detailsTextView = convertView.findViewById(R.id.exercise_details_text);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteExerciseButton);
        CheckBox checkBox = convertView.findViewById(R.id.exerciseCheckBox);

        if (exercise != null) {
            nameTextView.setText(exercise.getName());
            String details = String.format(
                    "%d serii x %d powt. — %02d:%02d",
                    exercise.getSets(),
                    exercise.getReps(),
                    exercise.getMinutes(),
                    exercise.getSeconds()
            );
            detailsTextView.setText(details);
        }

        if (selectionMode) {
            checkBox.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        } else {
            checkBox.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
        }

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(checkedExercises.contains(exercise));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!checkedExercises.contains(exercise)) {
                    checkedExercises.add(exercise);
                }
            } else {
                checkedExercises.remove(exercise);
            }
        });

        deleteButton.setOnClickListener(v -> {
            try (DatabaseHelper db = new DatabaseHelper(getContext())) {
                db.deleteExercise(exercise.getId());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Błąd podczas usuwania", Toast.LENGTH_SHORT).show();
                return;
            }
            exercises.remove(exercise);
            notifyDataSetChanged();
            Toast.makeText(getContext(), "Ćwiczenie usunięte", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    public List<Exercise> getCheckedExercises() {
        return checkedExercises;
    }
}
