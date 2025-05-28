package com.example.zdrowiepp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class TrainingPlanActivity extends AppCompatActivity {
    private ListView plansListView;
    private List<TrainingPlan> plans;
    private ArrayAdapter<TrainingPlan> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan);

        plansListView = findViewById(R.id.plansListView);

        Button buttonAddPlan = findViewById(R.id.buttonAddPlan);
        buttonAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateTrainingPlanActivity.class);
            startActivity(intent);
        });

        plansListView.setOnItemClickListener((adapterView, view, position, id) -> {
            TrainingPlan selectedPlan = (TrainingPlan) adapterView.getItemAtPosition(position);
            Intent intent = new Intent(this, CreateTrainingPlanActivity.class);
            intent.putExtra("PLAN_ID", selectedPlan.getId());
            startActivity(intent);
        });

        plansListView.setOnItemLongClickListener((parent, view, position, id) -> {
            TrainingPlan selectedPlan = plans.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Usuń plan")
                    .setMessage("Czy na pewno chcesz usunąć plan \"" + selectedPlan.getName() + "\"?")
                    .setPositiveButton("Usuń", (dialog, which) -> {
                        try (DatabaseHelper db = new DatabaseHelper(this)) {
                            db.deleteTrainingPlan(selectedPlan.getId());
                            Toast.makeText(this, "Plan usunięty", Toast.LENGTH_SHORT).show();
                            loadPlans();
                        }
                    })
                    .setNegativeButton("Anuluj", null)
                    .show();

            return true; // ważne: oznacza, że klik został "obsłużony"
        });
    }

    private void loadPlans() {
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            plans = db.selectAllTrainingPlans();
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, plans);
            plansListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlans();
    }
}
