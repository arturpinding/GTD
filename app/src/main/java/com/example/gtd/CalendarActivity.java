package com.example.gtd;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.ViewCompat;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarActivity extends BaseActivity {
    private static final String STATE_MONTH = "displayed_month";
    private static final String STATE_YEAR = "displayed_year";
    private final ArrayList<CalendarDay> calendarDays = new ArrayList<>();
    private final ArrayList<CalendarEntry> calendarEntries = new ArrayList<>();
    private CalendarDayAdapter adapter;
    private CalendarStorage storage;
    private Date today;
    private Calendar displayedMonth;
    private RecyclerView recyclerView;
    private TextView monthTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        setTitle(R.string.calendar);
        monthTitle = findViewById(R.id.calendarMonthTitle);
        ViewCompat.setAccessibilityHeading(monthTitle, true);
        monthTitle.setAccessibilityLiveRegion(android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE);

        Calendar systemDate = Calendar.getInstance();
        updateToday(systemDate);
        displayedMonth = (Calendar) systemDate.clone();
        if (savedInstanceState != null) {
            displayedMonth.set(Calendar.YEAR,
                    savedInstanceState.getInt(STATE_YEAR, systemDate.get(Calendar.YEAR)));
            displayedMonth.set(Calendar.MONTH,
                    savedInstanceState.getInt(STATE_MONTH, systemDate.get(Calendar.MONTH)));
        }
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
        storage = new CalendarStorage(this);

        recyclerView = findViewById(R.id.calendarRecyclerView);
        adapter = new CalendarDayAdapter(this, calendarDays);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        findViewById(R.id.calendarPreviousMonth).setOnClickListener(view -> changeMonth(-1));
        findViewById(R.id.calendarNextMonth).setOnClickListener(view -> changeMonth(1));
        findViewById(R.id.calendarTodayButton).setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            updateToday(now);
            displayedMonth = (Calendar) now.clone();
            displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
            refreshCalendar(true);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToday(Calendar.getInstance());
        refreshCalendar(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_MONTH, displayedMonth.get(Calendar.MONTH));
        outState.putInt(STATE_YEAR, displayedMonth.get(Calendar.YEAR));
        super.onSaveInstanceState(outState);
    }

    private void refreshCalendar(boolean resetScroll) {
        calendarEntries.clear();
        calendarEntries.addAll(storage.loadEntries());
        setUpCalendarDays();
        adapter.notifyDataSetChanged();
        if (resetScroll) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void setUpCalendarDays() {
        calendarDays.clear();
        monthTitle.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                .format(displayedMonth.getTime()));

        Calendar cursor = (Calendar) displayedMonth.clone();
        int offsetFromMonday = (cursor.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        cursor.add(Calendar.DAY_OF_MONTH, -offsetFromMonday);
        for (int i = 0; i < 42; i++) {
            Date date = new Date(cursor.get(Calendar.DAY_OF_MONTH), cursor.get(Calendar.MONTH),
                    cursor.get(Calendar.YEAR));
            boolean inDisplayedMonth = cursor.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH)
                    && cursor.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR);
            ArrayList<CalendarEntry> entries = new ArrayList<>();
            for (CalendarEntry entry : calendarEntries) {
                if (entry.getDate().equals(date)) entries.add(entry);
            }
            calendarDays.add(new CalendarDay(date, inDisplayedMonth, date.equals(today), entries));
            cursor.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void changeMonth(int amount) {
        displayedMonth.add(Calendar.MONTH, amount);
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
        refreshCalendar(true);
    }

    private void updateToday(Calendar calendar) {
        today = new Date(calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }
}
