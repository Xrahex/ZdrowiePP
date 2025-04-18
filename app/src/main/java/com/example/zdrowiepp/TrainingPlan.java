package com.example.zdrowiepp;

import java.util.HashMap;

public final class TrainingPlan {
    private final HashMap<Integer, Exercise> exercises = new HashMap<>();

    public void setExercise(Exercise exercise) {
        var id = exercise.getId();
        if (id == 0) {
            return;
        }

        exercises.put(id, exercise);
    }

    public Exercise getExercise(int id) {
        if (id == 0) {
            return null;
        }

        return exercises.get(id);
    }
}
