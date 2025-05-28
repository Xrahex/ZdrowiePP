package com.example.zdrowiepp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ChartHelper {

    public static Bitmap createStepsChartBitmap(int[] stepsData, Context context) {
        LineChart chart = new LineChart(context);
        chart.setLayoutParams(new android.widget.LinearLayout.LayoutParams(600, 400)); // rozmiar bitmapy

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < stepsData.length; i++) {
            entries.add(new Entry(i, stepsData[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Kroki - ostatnie 7 dni");
        dataSet.setColor(context.getResources().getColor(R.color.purple_700));
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.enableDashedLine(10f, 5f, 0f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(600, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(400, android.view.View.MeasureSpec.EXACTLY));
        chart.layout(0, 0, 600, 400);

        Bitmap bitmap = Bitmap.createBitmap(600, 400, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        chart.draw(canvas);

        return bitmap;
    }
}
