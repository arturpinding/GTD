package com.example.gtd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class WeeklyReviewActivity extends Activity {
    private static final String PREFS_NAME = "WeeklyReviewPrefs";
    private final int[] checkBoxIds = {
            R.id.reviewInbox, R.id.reviewCalendar, R.id.reviewNextActions,
            R.id.reviewProjects, R.id.reviewWaitingFor, R.id.reviewSomeday
    };
    private SharedPreferences preferences;
    private GtdRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_review_activity);
        setTitle(R.string.weekly_review);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        repository = new GtdRepository(this);

        for (int id : checkBoxIds) {
            CheckBox checkBox = findViewById(id);
            checkBox.setChecked(preferences.getBoolean(String.valueOf(id), false));
            checkBox.setOnCheckedChangeListener((button, checked) ->
                    preferences.edit().putBoolean(String.valueOf(button.getId()), checked).apply());
        }
        Button reset = findViewById(R.id.weeklyReviewReset);
        reset.setOnClickListener(view -> {
            preferences.edit().clear().apply();
            for (int id : checkBoxIds) ((CheckBox) findViewById(id)).setChecked(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int inboxCount = 0;
        String inbox = getSharedPreferences("InboxPrefs", Context.MODE_PRIVATE)
                .getString("items", "");
        if (inbox != null && !inbox.isEmpty()) {
            for (String item : inbox.split("\\|\\|")) if (!item.isEmpty()) inboxCount++;
        }
        String summary = getString(R.string.weekly_review_counts,
                inboxCount,
                repository.count(GtdRepository.PROJECTS, false),
                repository.count(GtdRepository.NEXT_ACTIONS, false),
                repository.count(GtdRepository.WAITING_FOR, false),
                repository.count(GtdRepository.SOMEDAY_MAYBE, false));
        ((TextView) findViewById(R.id.weeklyReviewSummary)).setText(summary);
    }
}
