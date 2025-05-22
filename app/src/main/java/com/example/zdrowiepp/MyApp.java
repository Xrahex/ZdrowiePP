package com.example.zdrowiepp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {

    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_APP_THEME = "app_theme";
    private static final String KEY_USER_ID = "user_id";

    private static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int theme = prefs.getInt(KEY_APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    public static void saveUserId(Context context, int userId) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        }
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public static int getUserId(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        }
        return prefs.getInt(KEY_USER_ID, -1);
    }
}
