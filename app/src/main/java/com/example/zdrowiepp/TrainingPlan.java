package com.example.zdrowiepp;

import android.content.Context;

public class TrainingPlan {
    private int id;
    private String name;
    private int userId;

    public TrainingPlan(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public TrainingPlan(Context context, int id) {
        try (DatabaseHelper db = new DatabaseHelper(context)) {
            TrainingPlan tp = db.selectTrainingPlan(id);
            if (tp != null) {
                this.id = tp.id;
                this.name = tp.name;
                this.userId = tp.userId;
            }
        }
    }

    public void saveTrainingPlan(Context context) {
        try (DatabaseHelper db = new DatabaseHelper(context)) {
            int newId = db.insertTrainingPlan(this);
            this.id = newId;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public int fetchLatestId(Context context) {
        try (DatabaseHelper db = new DatabaseHelper(context)) {
            return db.getLastInsertedTrainingPlanId(userId);
        }
    }

    // Gettery i settery
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
