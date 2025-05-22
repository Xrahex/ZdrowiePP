package com.example.zdrowiepp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepCounting extends AppCompatActivity {

    private TextView stepCountText;
    private LineChart lineChart;

    private DatabaseHelper dbHelper;
    private int userId;
    private int[] last7DaysSteps = new int[]{0, 0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counting);

        dbHelper = new DatabaseHelper(this);
        userId = MyApp.getUserId(getApplicationContext());

        if (userId == -1) {
            Toast.makeText(this, "Nie zalogowano użytkownika!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        stepCountText = findViewById(R.id.stepCountText);
        lineChart = findViewById(R.id.lineChart);

        loadTodaySteps();
        loadLast7DaysSteps();
        setupChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(stepsReceiver,
                new IntentFilter("com.example.zdrowiepp.STEPS_UPDATED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepsReceiver);
    }


    private void loadTodaySteps() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        StepEntry todayEntry = StepEntry.getForDate(dbHelper, userId, todayDate);
        int steps = (todayEntry != null) ? todayEntry.getCount() : 0;
        String text = getString(R.string.stepsToday, steps);
        stepCountText.setText(text);
        last7DaysSteps[6] = steps;
    }

    private void loadLast7DaysSteps() {
        List<StepEntry> entries = StepEntry.getLast7Days(dbHelper, userId);
        for (int i = 0; i < last7DaysSteps.length; i++) last7DaysSteps[i] = 0;

        int startIndex = last7DaysSteps.length - entries.size();
        for (int i = 0; i < entries.size(); i++) {
            last7DaysSteps[startIndex + i] = entries.get(entries.size() - 1 - i).getCount();
        }
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);

        updateChart();
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < last7DaysSteps.length; i++) {
            entries.add(new Entry(i, last7DaysSteps[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Kroki - ostatnie 7 dni");
        dataSet.setColor(getResources().getColor(R.color.purple_700));
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.enableDashedLine(10f, 5f, 0f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver stepsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.zdrowiepp.STEPS_UPDATED".equals(intent.getAction())) {
                // odczytaj aktualne dane kroków z bazy, nie tylko z Intentu
                loadLast7DaysSteps();
                loadTodaySteps(); // zaktualizuj TextView
                updateChart();
            }
        }
    };
}
