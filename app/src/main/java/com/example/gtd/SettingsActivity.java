package com.example.gtd;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
    private GtdRepository repository;
    private TextView summary;
    private Button clearCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setTitle(R.string.settings);
        repository = new GtdRepository(this);
        summary = findViewById(R.id.settingsSummary);

        clearCompleted = findViewById(R.id.clearCompletedButton);
        clearCompleted.setOnClickListener(view -> confirmClearCompleted());

        Button clearAll = findViewById(R.id.clearAllGtdButton);
        clearAll.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all_gtd)
                .setMessage(R.string.clear_all_gtd_warning)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    repository.clearAll();
                    Toast.makeText(this, R.string.gtd_lists_cleared, Toast.LENGTH_SHORT).show();
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
        int completed = repository.countCompleted();
        clearCompleted.setEnabled(completed > 0);
        clearCompleted.setText(getResources().getQuantityString(
                R.plurals.clear_completed_count, completed, completed));
        summary.setText(getString(R.string.settings_counts,
                repository.count(GtdRepository.PROJECTS, true),
                repository.count(GtdRepository.NEXT_ACTIONS, true),
                repository.count(GtdRepository.WAITING_FOR, true),
                repository.count(GtdRepository.SOMEDAY_MAYBE, true),
                repository.count(GtdRepository.REFERENCE, true)));
    }

    private void confirmClearCompleted() {
        int count = repository.countCompleted();
        if (count == 0) return;
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_completed)
                .setMessage(getResources().getQuantityString(
                        R.plurals.clear_completed_confirmation, count, count))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    int removed = repository.clearCompleted();
                    Toast.makeText(this, getResources().getQuantityString(
                            R.plurals.completed_removed, removed, removed), Toast.LENGTH_SHORT).show();
                    updateSummary();
                })
                .show();
    }
}
