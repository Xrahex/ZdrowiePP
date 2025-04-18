package com.example.zdrowiepp;

import android.content.Context;

public final class Exercise {
    private int id = 0;

    private int trainingPlanId = 0;
    private String name;
    private short reps;
    private short sets;
    private byte seconds;
    private byte minutes;

    public Exercise(int id, int trainingPlanId, String name, byte minutes, byte seconds, short sets, short reps) {
        this.id = id;
        this.trainingPlanId = trainingPlanId;
        this.name = name;
        this.minutes = minutes;
        this.seconds = seconds;
        this.sets = sets;
        this.reps = reps;
    }

    public Exercise(Context context, int id) {
        try (var db = new DatabaseHelper(context)) {
            var exercise = db.selectExercise(id);
            if (exercise == null) {
                return;
            }

            this.id = id;
            trainingPlanId = exercise.trainingPlanId;
            name = exercise.name;
            minutes = exercise.minutes;
            seconds = exercise.seconds;
            sets = exercise.sets;
            reps = exercise.reps;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingPlanId() {
        return trainingPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getReps() {
        return reps;
    }

    public void setReps(short reps) {
        this.reps = reps;
    }

    public byte getSeconds() {
        return seconds;
    }

    public void setSeconds(byte seconds) {
        this.seconds = seconds;
    }

    public short getSets() {
        return sets;
    }

    public void setSets(short sets) {
        this.sets = sets;
    }

    public byte getMinutes() {
        return minutes;
    }

    public void setMinutes(byte minutes) {
        this.minutes = minutes;
    }

    public void saveExercise(Context context, int trainingPlanId, String name, byte minutes, byte seconds, short sets, short reps) {
        this.trainingPlanId = trainingPlanId;
        this.name = name;
        this.minutes = minutes;
        this.seconds = seconds;
        this.sets = sets;
        this.reps = reps;

        try (var db = new DatabaseHelper(context)) {
            db.insertExercise(this);
        }
    }
}