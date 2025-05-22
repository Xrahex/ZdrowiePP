package com.example.zdrowiepp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class HomeActivity extends BaseActivity {

    private GridView gridView;

    // Dane dla kafelków

    private String[] tileTitles;

    private int[] tileIcons = {
            android.R.drawable.ic_menu_agenda,      // treningi
            android.R.drawable.ic_menu_directions,  // steps
            android.R.drawable.ic_menu_camera,      // aparat
            android.R.drawable.ic_menu_preferences,  // ustawienia
            android.R.drawable.ic_menu_edit,   // ikona archiwum
            android.R.drawable.ic_menu_preferences,
    };
    private Class<?>[] activities = {
            CreateTrainingPlanActivity.class,
            StepCounting.class,
            CameraActivity.class,
            SettingsActivity.class,
            // ListsActivity.class,
            // TemplatesActivity.class,
            // ThemeActivity.class,
            // RouteActivity.class,
            // ArchiveActivity.class,
            // StatsActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tileTitles = new String[] {
                getString(R.string.treningi),
                getString(R.string.steps),
                getString(R.string.aparat),
                getString(R.string.ustawienia),
                "Wyznacz trasę",
                "Archiwum",
        };


        gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new TileAdapter());


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < activities.length) {
                    // Uruchom przypisaną aktywność
                    Intent intent = new Intent(HomeActivity.this, activities[position]);
                    startActivity(intent);
                } else {
                    // Ewentualnie pokaż komunikat dla kafelków bez aktywności
                    // Toast.makeText(HomeActivity.this, "Funkcja w trakcie tworzenia", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            recreate();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings_prefs", MODE_PRIVATE);
        String lang = prefs.getString("app_language", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);

        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    // Adapter dla GridView
    private class TileAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tileTitles.length;
        }

        @Override
        public Object getItem(int position) {
            return tileTitles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View tileView = convertView;
            if (tileView == null) {
                tileView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }

            ImageView icon = tileView.findViewById(R.id.icon);
            TextView title = tileView.findViewById(R.id.title);

            icon.setImageResource(tileIcons[position]);
            title.setText(tileTitles[position]);

            return tileView;
        }
    }
}