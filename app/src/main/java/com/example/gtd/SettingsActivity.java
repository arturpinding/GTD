package com.example.gtd;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    private GtdRepository repository;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setTitle(R.string.settings);
        repository = new GtdRepository(this);
        summary = findViewById(R.id.settingsSummary);

        Button clearCompleted = findViewById(R.id.clearCompletedButton);
        clearCompleted.setOnClickListener(view -> {
            int removed = repository.clearCompleted();
            Toast.makeText(this, getString(R.string.completed_removed, removed), Toast.LENGTH_SHORT).show();
            updateSummary();
        });

        Button clearAll = findViewById(R.id.clearAllGtdButton);
        clearAll.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all_gtd)
                .setMessage(R.string.clear_all_gtd_warning)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    repository.clearAll();
                    updateSummary();
                })
                .show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        summary.setText(getString(R.string.settings_counts,
                repository.count(GtdRepository.PROJECTS, true),
                repository.count(GtdRepository.NEXT_ACTIONS, true),
                repository.count(GtdRepository.WAITING_FOR, true),
                repository.count(GtdRepository.SOMEDAY_MAYBE, true),
                repository.count(GtdRepository.REFERENCE, true)));
    }
}
