package com.example.zdrowiepp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastAcceleration = 0f;
    private float currentAcceleration = 0f;
    private float acceleration = 0f;
    private long lastStepTime = 0;
    private final long STEP_DELAY_MS = 500;
    private int stepsToday = 0;
    private DatabaseHelper dbHelper;

    private static final String CHANNEL_ID = "step_counter_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        createNotificationChannel();
        startForeground(1, getNotification(stepsToday));

        loadStepsForToday();

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void loadStepsForToday() {
        int userId = MyApp.getUserId(getApplicationContext());
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        StepEntry entry = StepEntry.getForDate(dbHelper, userId, today);
        stepsToday = entry != null ? entry.getCount() : 0;
    }

    private Notification getNotification(int steps) {
        Log.d("StepCounterService", "Tworzę powiadomienie");
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Licznik kroków")
                .setContentText("Aktualna liczba kroków: " + steps)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Step Counter";
            String description = "Kanał licznika kroków";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + delta;

        if (acceleration > 2.5f) {
            long now = System.currentTimeMillis();
            if (now - lastStepTime > STEP_DELAY_MS) {
                lastStepTime = now;
                stepsToday++;
                saveStepsToDatabase();
                startForeground(1, getNotification(stepsToday));
                notifyStepCountChanged();
            }
        }
    }

    private void saveStepsToDatabase() {
        int userId = MyApp.getUserId(getApplicationContext());
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        StepEntry entry = new StepEntry(userId, stepsToday, today);
        entry.save(dbHelper);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyStepCountChanged() {
        Intent intent = new Intent("com.example.zdrowiepp.STEPS_UPDATED");
        intent.putExtra("steps", stepsToday);
        //sendBroadcast(intent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
