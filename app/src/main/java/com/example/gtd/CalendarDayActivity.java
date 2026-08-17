package com.example.gtd;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarDayActivity extends BaseActivity implements CalendarEntryAdapter.Listener {
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
        Calendar displayDate = new GregorianCalendar(year, month, day);
        setTitle(java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)
                .format(displayDate.getTime()));
        ((TextView) findViewById(R.id.calendarDayTitle)).setText(
                java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL)
                        .format(displayDate.getTime()));
        emptyView = findViewById(R.id.calendarDayEmpty);
        titleInput = findViewById(R.id.calendarEntryTitleInput);
        timeInput = findViewById(R.id.calendarEntryTimeInput);
        timeInput.setShowSoftInputOnFocus(false);
        storage = new CalendarStorage(this);

        RecyclerView recyclerView = findViewById(R.id.calendarDayRecyclerView);
        adapter = new CalendarEntryAdapter(this, dayEntries, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        timeInput.setOnClickListener(view -> showTimePicker(timeInput));
        Button addButton = findViewById(R.id.calendarEntryAddButton);
        addButton.setOnClickListener(view -> addEntry());
        titleInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addEntry();
                return true;
            }
            return false;
        });
        reload();
    }

    private void showTimePicker(EditText target) {
        Calendar now = Calendar.getInstance();
        int selectedMinutes = parseTime(getNormalizedTime(target));
        int initialHour = selectedMinutes == Integer.MAX_VALUE
                ? now.get(Calendar.HOUR_OF_DAY) : selectedMinutes / 60;
        int initialMinute = selectedMinutes == Integer.MAX_VALUE
                ? now.get(Calendar.MINUTE) : selectedMinutes % 60;
        new TimePickerDialog(this, (picker, hour, minute) -> {
                String normalized = String.format(Locale.ROOT, "%02d:%02d", hour, minute);
                target.setTag(normalized);
                target.setText(formatTimeForDisplay(normalized));
            },
                initialHour, initialMinute,
                android.text.format.DateFormat.is24HourFormat(this)).show();
    }

    private int parseTime(String value) {
        CalendarEntry temporary = new CalendarEntry("", value, selectedDate);
        return temporary.getMinutesSinceMidnight();
    }

    private void addEntry() {
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            titleInput.setError(getString(R.string.enter_something));
            return;
        }
        allEntries.add(new CalendarEntry(title, getNormalizedTime(timeInput), selectedDate));
        storage.saveEntries(allEntries);
        titleInput.setText("");
        timeInput.setText("");
        timeInput.setTag(null);
        reload();
    }

    private void reload() {
        allEntries.clear();
        allEntries.addAll(storage.loadEntries());
        dayEntries.clear();
        for (CalendarEntry entry : allEntries) {
            if (selectedDate.equals(entry.getDate())) dayEntries.add(entry);
        }
        Collections.sort(dayEntries, Comparator.comparingInt(
                CalendarEntry::getMinutesSinceMidnight));
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(dayEntries.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEntryActionRequested(CalendarEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle(entry.getText())
                .setItems(new String[]{getString(R.string.edit), getString(R.string.delete)},
                        (dialog, which) -> {
                            if (which == 0) editEntry(entry);
                            else confirmDelete(entry);
                        })
                .show();
    }

    private void editEntry(CalendarEntry entry) {
        LinearLayout inputs = new LinearLayout(this);
        inputs.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_input_padding);
        inputs.setPadding(padding, padding / 2, padding, 0);
        EditText time = new EditText(this);
        time.setHint(R.string.time_hint);
        time.setInputType(android.text.InputType.TYPE_CLASS_DATETIME
                | android.text.InputType.TYPE_DATETIME_VARIATION_TIME);
        time.setTag(entry.getTime());
        time.setText(formatTimeForDisplay(entry.getTime()));
        time.setShowSoftInputOnFocus(false);
        time.setOnClickListener(view -> showTimePicker(time));
        EditText title = new EditText(this);
        title.setHint(R.string.calendar_entry_hint);
        title.setText(entry.getText());
        title.setSelectAllOnFocus(true);
        inputs.addView(time);
        inputs.addView(title);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_event)
                .setView(inputs)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.clear_time, null)
                .setPositiveButton(R.string.save, null)
                .create();
        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setOnClickListener(view -> {
                        time.setTag("");
                        time.setText("");
                    });
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                    String updatedTitle = title.getText().toString().trim();
                    if (updatedTitle.isEmpty()) {
                        title.setError(getString(R.string.enter_something));
                        return;
                    }
                    entry.setText(updatedTitle);
                    entry.setTime(getNormalizedTime(time));
                    storage.saveEntries(allEntries);
                    dialog.dismiss();
                    reload();
                });
        });
        dialog.show();
    }

    private String getNormalizedTime(EditText input) {
        Object tag = input.getTag();
        if (tag instanceof String) return (String) tag;
        int minutes = parseTime(input.getText().toString().trim());
        return minutes == Integer.MAX_VALUE ? ""
                : String.format(Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60);
    }

    private String formatTimeForDisplay(String normalized) {
        int minutes = parseTime(normalized);
        if (minutes == Integer.MAX_VALUE) return normalized == null ? "" : normalized;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
        calendar.set(Calendar.MINUTE, minutes % 60);
        return android.text.format.DateFormat.getTimeFormat(this).format(calendar.getTime());
    }

    private void confirmDelete(CalendarEntry entry) {
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
