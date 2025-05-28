package com.example.zdrowiepp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExerciseHistoryAdapter extends ArrayAdapter<ExerciseHistoryItem> {

    public ExerciseHistoryAdapter(Context context, List<ExerciseHistoryItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExerciseHistoryItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        text1.setText(item.getExerciseName());
        text2.setText("Data: " + sdf.format(item.getDate()) +
                ", Serie: " + item.getSets() +
                ", Czas: " + item.getHours() + "h " + item.getMinutes() + "min");

        return convertView;
    }
}
