package com.example.zdrowiepp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        // Aktualizacja języka
        SharedPreferences prefs = newBase.getSharedPreferences("settings_prefs", MODE_PRIVATE);
        String lang = prefs.getString("app_language", "en");
        super.attachBaseContext(updateLocale(newBase, lang));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Aktualizacja motywu
        SharedPreferences prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        int theme = prefs.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);

        super.onCreate(savedInstanceState);
    }

    private Context updateLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}