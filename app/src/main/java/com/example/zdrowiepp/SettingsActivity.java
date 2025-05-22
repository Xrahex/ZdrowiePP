package com.example.zdrowiepp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "settings_prefs";
    private static final String THEME_KEY = "app_theme";
    private static final String LANGUAGE_KEY = "app_language";
    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            if (settingsChanged) {
                restartApp();
            } else {
                finish();
            }
        });

        // Switch motywu
        Switch themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setChecked(isDarkThemeEnabled());
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveTheme(isChecked);
            settingsChanged = true;
        });

        // Spinner języka
        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.setSelection(getCurrentLanguagePosition());
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newLang = position == 0 ? "pl" : "en";
                if (!newLang.equals(getCurrentLanguage())) {
                    saveLanguage(newLang);
                    settingsChanged = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean isDarkThemeEnabled() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                == AppCompatDelegate.MODE_NIGHT_YES;
    }

    private String getCurrentLanguage() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "en");
    }

    private void saveTheme(boolean isDark) {
        int mode = isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putInt(THEME_KEY, mode)
                .apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private int getCurrentLanguagePosition() {
        return getCurrentLanguage().equals("pl") ? 0 : 1;
    }

    private void saveLanguage(String lang) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(LANGUAGE_KEY, lang)
                .apply();
    }

    private void restartApp() {
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finishAffinity();
    }
}