package com.example.gtd;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DateFormat;
public class WeeklyReviewActivity extends BaseActivity {
    private static final String PREFS_NAME = "WeeklyReviewPrefs";
    private static final String LAST_COMPLETED_KEY = "last_completed_at";
    private static final String COMPLETION_RECORDED_KEY = "completion_recorded";
    private final int[] checkBoxIds = {
            R.id.reviewInbox, R.id.reviewCalendar, R.id.reviewNextActions,
            R.id.reviewProjects, R.id.reviewWaitingFor, R.id.reviewSomeday
    };
    private final String[] stepKeys = {
            "inbox", "calendar", "next_actions", "projects", "waiting_for", "someday_maybe"
    };
    private final int[] destinationLabels = {
            R.string.inbox, R.string.calendar, R.string.next_actions,
            R.string.projects, R.string.waiting_for, R.string.someday_maybe
    };
    private final Class<?>[] destinationActivities = {
            InboxActivity.class, CalendarActivity.class, NextActionsActivity.class,
            ProjectsActivity.class, WaitingForActivity.class, SomedayMaybeActivity.class
    };
    private SharedPreferences preferences;
    private GtdRepository repository;
    private Button openStepButton;
    private boolean restoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_review_activity);
        setTitle(R.string.weekly_review);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        repository = new GtdRepository(this);
        openStepButton = findViewById(R.id.weeklyReviewOpenStep);

        restoring = true;
        for (int i = 0; i < checkBoxIds.length; i++) {
            CheckBox checkBox = findViewById(checkBoxIds[i]);
            checkBox.setChecked(preferences.getBoolean(stepKeys[i], false));
            final String key = stepKeys[i];
            checkBox.setOnCheckedChangeListener((button, checked) -> {
                if (!restoring) {
                    preferences.edit().putBoolean(key, checked).apply();
                    updateProgress();
                }
            });
        }
        restoring = false;

        openStepButton.setOnClickListener(view -> openCurrentStep());
        findViewById(R.id.weeklyReviewReset).setOnClickListener(view -> confirmNewReview());
        updateProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int inboxCount = repository.count(GtdRepository.INBOX, false);
        int stalledProjects = repository.countProjectsWithoutNextAction();
        String summary = getString(R.string.weekly_review_counts,
                inboxCount,
                repository.count(GtdRepository.PROJECTS, false),
                repository.count(GtdRepository.NEXT_ACTIONS, false),
                repository.count(GtdRepository.WAITING_FOR, false),
                repository.count(GtdRepository.SOMEDAY_MAYBE, false),
                stalledProjects);
        ((TextView) findViewById(R.id.weeklyReviewSummary)).setText(summary);
        updateProgress();
    }

    private void updateProgress() {
        int complete = 0;
        for (int id : checkBoxIds) {
            if (((CheckBox) findViewById(id)).isChecked()) complete++;
        }
        ((TextView) findViewById(R.id.weeklyReviewProgress)).setText(
                getString(R.string.review_progress, complete, checkBoxIds.length));

        if (complete == checkBoxIds.length) {
            long lastCompleted = preferences.getLong(LAST_COMPLETED_KEY, 0L);
            if (!preferences.getBoolean(COMPLETION_RECORDED_KEY, false)) {
                lastCompleted = System.currentTimeMillis();
                preferences.edit()
                        .putLong(LAST_COMPLETED_KEY, lastCompleted)
                        .putBoolean(COMPLETION_RECORDED_KEY, true)
                        .apply();
            }
            openStepButton.setText(R.string.review_complete);
            openStepButton.setEnabled(false);
        } else {
            int step = firstIncompleteStep();
            openStepButton.setText(getString(R.string.open_review_step,
                    getString(destinationLabels[step])));
            openStepButton.setEnabled(true);
        }

        long lastCompleted = preferences.getLong(LAST_COMPLETED_KEY, 0L);
        TextView lastReview = findViewById(R.id.weeklyReviewLastCompleted);
        lastReview.setText(lastCompleted == 0L
                ? getString(R.string.no_completed_review)
                : getString(R.string.last_review_completed,
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                .format(new java.util.Date(lastCompleted))));
    }

    private int firstIncompleteStep() {
        for (int i = 0; i < checkBoxIds.length; i++) {
            if (!((CheckBox) findViewById(checkBoxIds[i])).isChecked()) return i;
        }
        return 0;
    }

    private void openCurrentStep() {
        int step = firstIncompleteStep();
        startActivity(new Intent(this, destinationActivities[step]));
    }

    private void confirmNewReview() {
        boolean hasProgress = false;
        for (int id : checkBoxIds) {
            if (((CheckBox) findViewById(id)).isChecked()) {
                hasProgress = true;
                break;
            }
        }
        if (!hasProgress) {
            resetReview();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.start_new_review)
                .setMessage(R.string.start_new_review_confirmation)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.start, (dialog, which) -> resetReview())
                .show();
    }

    private void resetReview() {
        restoring = true;
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < checkBoxIds.length; i++) {
            editor.remove(stepKeys[i]);
            ((CheckBox) findViewById(checkBoxIds[i])).setChecked(false);
        }
        editor.remove(COMPLETION_RECORDED_KEY);
        editor.apply();
        restoring = false;
        updateProgress();
    }
}
