package com.example.zdrowiepp;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    private DatabaseHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);  // czysta baza
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    @Test
    public void testInsertAndCheckUser() {
        boolean inserted = dbHelper.insertUser("test@example.com", "password123");
        assertTrue(inserted);

        boolean exists = dbHelper.checkUser("test@example.com", "password123");
        assertTrue(exists);
    }

    @Test
    public void testInsertTrainingPlan() {
        int userId = dbHelper.getUserId("test@example.com");
        if (userId == -1) {
            dbHelper.insertUser("test@example.com", "pass");
            userId = dbHelper.getUserId("test@example.com");
        }
        TrainingPlan plan = new TrainingPlan(0, "Plan A", userId);
        int planId = dbHelper.insertTrainingPlan(plan);
        assertTrue(planId > 0);

        TrainingPlan loadedPlan = dbHelper.selectTrainingPlan(planId);
        assertNotNull(loadedPlan);
        assertEquals("Plan A", loadedPlan.getName());
        assertEquals(userId, loadedPlan.getUserId());
    }

    @Test
    public void testInsertAndSelectExercise() {
        dbHelper.insertUser("test2@example.com", "abc123");
        int userId = dbHelper.getUserId("test2@example.com");
        int planId = dbHelper.insertTrainingPlan(new TrainingPlan(0, "Test Plan", userId));

        Exercise exercise = new Exercise(0, planId, "Push-ups", (byte)0, (byte)30, (short)3, (short)15);
        dbHelper.insertExercise(exercise);

        List<Exercise> exercises = dbHelper.selectExercisesByTrainingPlanId(planId);
        assertFalse(exercises.isEmpty());
        assertEquals("Push-ups", exercises.get(0).getName());
    }
}
