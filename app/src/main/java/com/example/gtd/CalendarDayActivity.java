package com.example.gtd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarDayActivity extends Activity implements CalendarEntryAdapter.Listener {
    public static final String EXTRA_DAY = "com.example.gtd.EXTRA_DAY";
    public static final String EXTRA_MONTH = "com.example.gtd.EXTRA_MONTH";
    public static final String EXTRA_YEAR = "com.example.gtd.EXTRA_YEAR";

    private final ArrayList<CalendarEntry> allEntries = new ArrayList<>();
    private final ArrayList<CalendarEntry> dayEntries = new ArrayList<>();
    private CalendarStorage storage;
    private CalendarEntryAdapter adapter;
    private Date selectedDate;
    private TextView emptyView;
    private EditText titleInput;
    private EditText timeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_day_activity);

        int day = getIntent().getIntExtra(EXTRA_DAY, -1);
        int month = getIntent().getIntExtra(EXTRA_MONTH, -1);
        int year = getIntent().getIntExtra(EXTRA_YEAR, -1);
        if (day < 1 || month < 0 || month > 11 || year < 1) {
            finish();
            return;
        }
        selectedDate = new Date(day, month, year);
        setTitle(selectedDate.getMonth() + " " + day);

        ((TextView) findViewById(R.id.calendarDayTitle)).setText(
                getString(R.string.calendar_day_title, selectedDate.getMonth(), day, year));
        emptyView = findViewById(R.id.calendarDayEmpty);
        titleInput = findViewById(R.id.calendarEntryTitleInput);
        timeInput = findViewById(R.id.calendarEntryTimeInput);
        storage = new CalendarStorage(this);

        RecyclerView recyclerView = findViewById(R.id.calendarDayRecyclerView);
        adapter = new CalendarEntryAdapter(this, dayEntries, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        timeInput.setOnClickListener(view -> showTimePicker());
        Button addButton = findViewById(R.id.calendarEntryAddButton);
        addButton.setOnClickListener(view -> addEntry());
        reload();
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        new TimePickerDialog(this, (picker, hour, minute) ->
                timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
    }

    private void addEntry() {
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            titleInput.setError(getString(R.string.enter_something));
            return;
        }
        allEntries.add(new CalendarEntry(title, timeInput.getText().toString().trim(), selectedDate));
        storage.saveEntries(allEntries);
        titleInput.setText("");
        timeInput.setText("");
        reload();
    }

    private void reload() {
        allEntries.clear();
        allEntries.addAll(storage.loadEntries());
        dayEntries.clear();
        for (CalendarEntry entry : allEntries) {
            if (selectedDate.equals(entry.getDate())) dayEntries.add(entry);
        }
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(dayEntries.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEntryDeleteRequested(CalendarEntry entry) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_item_question, entry.getText()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    allEntries.remove(entry);
                    storage.saveEntries(allEntries);
                    reload();
                })
                .show();
    }
}
