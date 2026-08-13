package com.example.gtd;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TodayActivity extends Activity implements GtdItemAdapter.Listener {
    private final ArrayList<CalendarEntry> schedule = new ArrayList<>();
    private final ArrayList<GtdItem> focusItems = new ArrayList<>();
    private GtdRepository repository;
    private CalendarStorage calendarStorage;
    private GtdItemAdapter focusAdapter;
    private CalendarEntryAdapter scheduleAdapter;
    private TextView scheduleEmpty;
    private TextView focusEmpty;
    private EditText quickActionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_activity);
        setTitle(R.string.today);
        repository = new GtdRepository(this);
        calendarStorage = new CalendarStorage(this);

        ((TextView) findViewById(R.id.todayDate)).setText(
                DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));
        scheduleEmpty = findViewById(R.id.todayScheduleEmpty);
        focusEmpty = findViewById(R.id.todayFocusEmpty);
        quickActionInput = findViewById(R.id.todayQuickActionInput);

        RecyclerView scheduleView = findViewById(R.id.todayScheduleRecyclerView);
        scheduleAdapter = new CalendarEntryAdapter(this, schedule);
        scheduleView.setAdapter(scheduleAdapter);
        scheduleView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView focusView = findViewById(R.id.todayFocusRecyclerView);
        focusAdapter = new GtdItemAdapter(focusItems, true, true, this);
        focusView.setAdapter(focusAdapter);
        focusView.setLayoutManager(new LinearLayoutManager(this));

        Button addButton = findViewById(R.id.todayQuickAddButton);
        addButton.setOnClickListener(view -> addQuickAction());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void addQuickAction() {
        String title = quickActionInput.getText().toString().trim();
        if (title.isEmpty()) {
            quickActionInput.setError(getString(R.string.enter_something));
            return;
        }
        repository.add(title, GtdRepository.NEXT_ACTIONS, true);
        quickActionInput.setText("");
        reload();
    }

    private void reload() {
        Calendar now = Calendar.getInstance();
        Date today = new Date(now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH), now.get(Calendar.YEAR));
        schedule.clear();
        schedule.addAll(calendarStorage.entriesFor(today));
        focusItems.clear();
        focusItems.addAll(repository.getTodayItems());
        scheduleAdapter.notifyDataSetChanged();
        focusAdapter.notifyDataSetChanged();
        scheduleEmpty.setVisibility(schedule.isEmpty() ? View.VISIBLE : View.GONE);
        focusEmpty.setVisibility(focusItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemChanged(GtdItem item) {
        repository.update(item);
        reload();
    }

    @Override
    public void onItemDeleteRequested(GtdItem item) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_item_question, item.getTitle()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    repository.delete(item);
                    reload();
                })
                .show();
    }
}
